package com.dd2d.talkingrecipe2.model.recipe

import android.content.Context
import android.net.Uri
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.toDTO
import com.dd2d.talkingrecipe2.isFromServer
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.IngredientUpsertField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.RecipeBasicInfoUpsertField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.StepInfoUpsertField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.IngredientTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.RecipeImageTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.RecipeTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.StepInfoImageTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.StepInfoTable
import com.dd2d.talkingrecipe2.removeEmptyIngredient
import com.dd2d.talkingrecipe2.removeEmptyStepInfo
import com.dd2d.talkingrecipe2.toByteArray
import com.dd2d.talkingrecipe2.toImagePath
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

interface RecipeUploadRepository {
    suspend fun uploadRecipe(
        recipe: Recipe,
        onChangeUploadState: (msg: String) -> Unit,
        onEndUpload: () -> Unit
    )
    suspend fun uploadRecipeBasicInfo(basicInfo: RecipeBasicInfo)
    suspend fun uploadRecipeIngredientList(recipeId: String, ingredientList: List<Ingredient>)
    suspend fun uploadRecipeStepInfoList(recipeId: String, stepInfoList: List<StepInfo>)
    suspend fun uploadRecipeThumbnail(recipeId: String, thumbnailUri: Uri)
}

/** @param context 이미지의 확장자 ( JPEG, PNG, GIF 등 )을 파악하기 위해서만 사용할 것.*/
class RecipeUploadRepositoryImpl(private val context: Context): RecipeUploadRepository {
    private val database = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ){
        install(Postgrest)
        install(Storage)
    }

    override suspend fun uploadRecipe(
        recipe: Recipe,
        onChangeUploadState: (msg: String) -> Unit,
        onEndUpload: ()->Unit
    ) {
        val (basicInfo, ingredientList, stepInfoList, thumbnailUri) = recipe
        val recipeId = basicInfo.recipeId

        withContext(Dispatchers.IO){
            try {
                onChangeUploadState("start uploading recipe basic info")
                uploadRecipeBasicInfo(basicInfo)

                onChangeUploadState("start uploading recipe ingredient list")
                uploadRecipeIngredientList(recipeId, ingredientList)

                onChangeUploadState("start uploading recipe step info list")
                uploadRecipeStepInfoList(recipeId, stepInfoList)

                onChangeUploadState("start uploading recipe thumbnail image")
                uploadRecipeThumbnail(recipeId, thumbnailUri)

                onEndUpload()
            }
            catch (e: Exception){
                throw IOException("IOException in uploadRecipe.\nmessage -> ${e.message}")
            }
        }
    }

    override suspend fun uploadRecipeBasicInfo(basicInfo: RecipeBasicInfo) {
        try {
            database
                .from(RecipeTable)
                .upsert(
                    value = basicInfo.toDTO(),
                    onConflict = RecipeBasicInfoUpsertField
                )
        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeBasicInfo.\nRecipeBasicInfo -> $basicInfo\nmessage -> ${e.message}")
        }
    }

    override suspend fun uploadRecipeIngredientList(recipeId: String, ingredientList: List<Ingredient>) {
        try {
            val upload = ingredientList.removeEmptyIngredient().toDTO(recipeId)

            database
                .from(IngredientTable)
                .upsert(upload, onConflict = IngredientUpsertField)
        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeIngredientList.\ningredientList -> $ingredientList\nmessage -> ${e.message}")
        }
    }

    override suspend fun uploadRecipeStepInfoList(recipeId: String, stepInfoList: List<StepInfo>) {
        val bucket = database
            .storage
            .from("${RecipeImageTable}/$recipeId/${StepInfoImageTable}")

        val upload = stepInfoList
            .removeEmptyStepInfo()
            .mapIndexed { order, stepInfo ->
                var imagePath = ""
                if (stepInfo.imageUri != Uri.EMPTY) {
                    imagePath = stepInfo.imageUri.toImagePath(
                        path = "$order",
                        context = context
                    )
                    try {
                        if(stepInfo.imageUri.isFromServer()){ /** 서버에서 받은 이미지를 재업로드*/
                            bucket.upload(data = stepInfo.imageUri.toByteArray(), path = imagePath, upsert = true)
                        }
                        else{ /** 갤러리에서 받은 이미지를 업로드*/
                            bucket.upload(uri = stepInfo.imageUri, path = imagePath, upsert = true)
                        }

                    } catch (e: Exception) {
                        throw IOException("IOException in uploadRecipeStepInfoList. - step info image\nstep info -> $stepInfo\nmessage -> ${e.message}")
                    }
                }
                stepInfo.toDTO(recipeId = recipeId, order = order, imagePath = imagePath)
            }
        try {
            database
                .from(StepInfoTable)
                .upsert(upload, onConflict = StepInfoUpsertField)
        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeStepInfoList.\nstepInfoList -> $stepInfoList\nmessage -> ${e.message}")
        }
    }

    override suspend fun uploadRecipeThumbnail(recipeId: String, thumbnailUri: Uri) {
        try {
            val imagePath = thumbnailUri
                .toImagePath(path = "${recipeId}_thumbnail", context = context)
            val bucket = database.storage.from("${RecipeImageTable}/$recipeId")

            if(thumbnailUri.isFromServer()){
                bucket.upload(data = thumbnailUri.toByteArray(), path = imagePath, upsert = true)
            }
            else{
                bucket.upload(uri = thumbnailUri, path = imagePath, upsert = true)
            }
        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeIngredientList.\nthumbnailUri -> $thumbnailUri\nmessage -> ${e.message}")
        }
    }
}