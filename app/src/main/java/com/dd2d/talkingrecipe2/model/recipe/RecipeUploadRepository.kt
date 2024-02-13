package com.dd2d.talkingrecipe2.model.recipe

import android.content.Context
import android.net.Uri
import android.util.Log
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.toDTO
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.BasicInfoUpsertField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.IngredientUpsertField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.StepInfoUpsertField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.IngredientTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.RecipeImageTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.RecipeTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.StepInfoImageTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.StepInfoTable
import com.dd2d.talkingrecipe2.removeEmptyIngredient
import com.dd2d.talkingrecipe2.removeEmptyStepInfo
import com.dd2d.talkingrecipe2.toImagePath
import com.dd2d.talkingrecipe2.uploadImage
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.IOException

/** 데이터베이스에 레시피를 업로드한다.
 * * [uploadRecipeBasicInfo]
 * * [uploadRecipeIngredientList]
 * * [uploadRecipeStepInfoList]
 * * [uploadRecipeThumbnail]*/
interface RecipeUploadRepository {
    suspend fun uploadRecipe(
        recipe: Recipe,
        onChangeUploadState: (msg: String) -> Unit,
        onEndUpload: () -> Unit
    )
    /** 레시피의 기본적인 정보를 업로드한다. [RecipeBasicInfo]*/
    suspend fun uploadRecipeBasicInfo(basicInfo: RecipeBasicInfo)
    /** 레시피의 재료 정보를 업로드한다. [Ingredient]*/
    suspend fun uploadRecipeIngredientList(recipeId: String, ingredientList: List<Ingredient>)
    /** 레시피의 단계 정보를 업로드한다. [StepInfo]*/
    suspend fun uploadRecipeStepInfoList(recipeId: String, stepInfoList: List<StepInfo>)
    /** 레시피의 썸네일을 업로드한다. [Uri]*/
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
                    onConflict = BasicInfoUpsertField
                )
        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeBasicInfo.\nRecipeBasicInfo -> $basicInfo\nmessage -> ${e.message}")
        }
    }

    override suspend fun uploadRecipeIngredientList(recipeId: String, ingredientList: List<Ingredient>) {
        try {
            val upload = ingredientList.removeEmptyIngredient().toDTO(recipeId)
            val version = 1
            database
                .from(IngredientTable)
                .upsert(
                    upload,
                    onConflict = IngredientUpsertField
                )

        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeIngredientList.\ningredientList -> $ingredientList\nmessage -> ${e.message}")
        }
    }

    override suspend fun uploadRecipeStepInfoList(recipeId: String, stepInfoList: List<StepInfo>) {
        val bucketApi = database
            .storage
            .from("${RecipeImageTable}/$recipeId/${StepInfoImageTable}")

        try {
            withContext(Dispatchers.IO){
                val upload = stepInfoList
                    .removeEmptyStepInfo()
                    .mapIndexed { order, stepInfo ->
                        async{
                            val imagePath = stepInfo.imageUri.toImagePath(path = "$order", context = context)

                            uploadImage(
                                bucketApi = bucketApi,
                                imageUri = stepInfo.imageUri,
                                uploadPath = imagePath,
                                callFrom = "uploadRecipeStepInfoList() - $order",
                                onTask = { task-> Log.d("LOG_CHECK", task) }
                            )
                            stepInfo.toDTO(recipeId = recipeId, order = order, imagePath = imagePath)

                        }
                    }.awaitAll()

                database
                    .from(StepInfoTable)
                    .upsert(upload, onConflict = StepInfoUpsertField)
            }
        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeStepInfoList.\nstepInfoList -> $stepInfoList\nmessage -> ${e.message}")
        }
    }

    override suspend fun uploadRecipeThumbnail(recipeId: String, thumbnailUri: Uri) {
        try {
            val bucketApi = database.storage.from("$RecipeImageTable/$recipeId")
            val imagePath = thumbnailUri.toImagePath(path = "${recipeId}_thumbnail", context = context)
            uploadImage(
                bucketApi = bucketApi,
                imageUri = thumbnailUri,
                uploadPath = imagePath,
                callFrom = "uploadRecipeThumbnail()",
                onTask = { task-> Log.d("LOG_CHECK", task) }
            )
        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeIngredientList.\nthumbnailUri -> $thumbnailUri\nmessage -> ${e.message}")
        }
    }
}