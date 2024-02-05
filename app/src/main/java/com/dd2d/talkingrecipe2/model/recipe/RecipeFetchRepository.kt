package com.dd2d.talkingrecipe2.model.recipe

import android.net.Uri
import androidx.core.net.toUri
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.IngredientDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfoDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfoDTO
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Expires.In30M
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.BasicInfoField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Filter.RecipeIdEqualTo
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.IngredientTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.RecipeImageTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.RecipeTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.StepInfoImageTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.StepInfoTable
import com.dd2d.talkingrecipe2.toSupabaseUrl
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

interface RecipeFetchRepository {
    suspend fun fetRecipeById(recipeId: String, onChangeFetchingState: (msg: String) -> Unit): Recipe
    suspend fun fetchRecipeBasicInfoById(recipeId: String): RecipeBasicInfo
    suspend fun fetchRecipeIngredientListById(recipeId: String): List<Ingredient>
    suspend fun fetchRecipeStepInfoListById(recipeId: String): List<StepInfo>
    suspend fun fetchRecipeThumbnailUriById(recipeId: String): Uri
}

class RecipeFetchRepositoryImpl: RecipeFetchRepository {
    private val database = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ){
        install(Postgrest)
        install(Storage)
    }

    override suspend fun fetRecipeById(
        recipeId: String,
        onChangeFetchingState: (msg: String) -> Unit
    ): Recipe {
        try {
            return withContext(Dispatchers.IO){
                onChangeFetchingState("start fetching recipe basic info")
                val basicInfo = async { fetchRecipeBasicInfoById(recipeId) }.await()

                onChangeFetchingState("start fetching recipe ingredient list")
                val ingredientList = async { fetchRecipeIngredientListById(recipeId).toMutableList() }.await()

                onChangeFetchingState("start fetching recipe step info list")
                val stepInfoList = async { fetchRecipeStepInfoListById(recipeId).toMutableList() }.await()

                onChangeFetchingState("start fetching recipe thumbnail image uri")
                val thumbnailUri = async { fetchRecipeThumbnailUriById(recipeId) }.await()

                Recipe(
                    basicInfo = basicInfo,
                    ingredientList = ingredientList,
                    stepInfoList = stepInfoList,
                    thumbnailUri = thumbnailUri
                )
            }
        }
        catch (e: Exception){
            throw IOException("IOException in fetRecipeById().\nrecipe id -> $recipeId.\nmessage -> ${e.message}")
        }
    }

    /** [recipeId]에 맞는 레시피의 [RecipeBasicInfo]를 받아옴.
     * @throws IOException*/
    override suspend fun fetchRecipeBasicInfoById(recipeId: String): RecipeBasicInfo {
        return withContext(Dispatchers.IO){
            database.from(RecipeTable)
                .select(columns = Columns.list(BasicInfoField)) {
                    filter {
                        eq(RecipeIdEqualTo, recipeId)
                    }
                }
                .decodeSingle<RecipeBasicInfoDTO>()
                .toRecipeBasicInfo()
        }
    }

    /** [recipeId]에 맞는 레시피의 [Ingredient] 리스트를 받아옴.
     * @throws IOException*/
    override suspend fun fetchRecipeIngredientListById(recipeId: String): List<Ingredient> {
        try {
            return withContext(Dispatchers.IO){
                database.from(IngredientTable)
                    .select {
                        filter {
                            eq(RecipeIdEqualTo, recipeId)
                        }
                    }
                    .decodeList<IngredientDTO>()
                    .map{ dto->
                        dto.toIngredient()
                    }
            }
        }
        catch (e: Exception){
            throw IOException("IOException in fetchRecipeIngredientListById().\nrecipe id -> $recipeId.\nmessage -> ${e.message}")
        }
    }
    /** [recipeId]에 맞는 레시피의 [StepInfo] 리스트를 받아옴.
     * [StepInfoTable]로부터 StepInfoDTO를 리스트 형태로 받은 후
     * 리스트를 순회하며 dto에 저장된 imagePath 값을 이용해 [RecipeImageTable]로부터 맞는 이미지를 가져옴
     * @throws IOException*/
    override suspend fun fetchRecipeStepInfoListById(recipeId: String): List<StepInfo> {
        try {
            return withContext(Dispatchers.IO){
                database.from(StepInfoTable)
                    .select {
                        filter {
                            eq(RecipeIdEqualTo, recipeId)
                        }
                    }
                    .decodeList<StepInfoDTO>()
                    .map { dto->
                        val imageUri = database
                            .storage
                            .from("${RecipeImageTable}/$recipeId/${StepInfoImageTable}")
                            .createSignedUrl(path = dto.imagePath, expiresIn = In30M)
                            .toSupabaseUrl()
                            .toUri()
                        dto.toStepInfo(imageUri = imageUri)
                    }
            }
        }
        catch (e: Exception){
            throw IOException("IOException in fetchRecipeStepInfoListById().\nrecipe id -> $recipeId.\nmessage -> ${e.message}")
        }
    }

    /** [recipeId]에 맞는 레시피의 썸네일 이미지를 [Uri]형태로 받아옴.
     * @throws IOException*/
    override suspend fun fetchRecipeThumbnailUriById(recipeId: String): Uri {
        try {
            return withContext(Dispatchers.IO){
                database.storage
                    .from("${RecipeImageTable}/$recipeId")
                    .createSignedUrl(path = "${recipeId}_thumbnail.jpeg", In30M)
                    .toSupabaseUrl()
                    .toUri()
            }
        }
        catch (e: Exception){
            throw IOException("IOException in fetchRecipeThumbnailUriById().\nrecipe id -> $recipeId.\nmessage -> ${e.message}")
        }
    }
}
