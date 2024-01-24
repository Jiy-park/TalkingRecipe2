package com.dd2d.talkingrecipe2.model

import android.content.Context
import android.net.Uri
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.createThumbnailImagePath
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.createStepInfoImagePath
import com.dd2d.talkingrecipe2.data_struct.recipe.toDTO
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Field.IngredientUpsertField
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Field.RecipeBasicInfoUpsertField
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Field.StepInfoUpsertField
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Table.IngredientTable
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Table.RecipeImageTable
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Table.RecipeTable
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Table.StepInfoImageTable
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Table.StepInfoTable
import com.dd2d.talkingrecipe2.removeEmptyElement
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/** @param context 이미지의 확장자 ( JPEG, PNG, GIF 등 )을 파악하기 위해서만 사용할 것.*/
class RecipeUploadRepository(private val context: Context): RecipeUpload {
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
            throw IOException("IOException in uploadRecipeBasicInfo.\nmessage -> ${e.message}")
        }
    }

    override suspend fun uploadRecipeIngredientList(recipeId: String, ingredientList: List<Ingredient>) {
        try {
            val upload = ingredientList.removeEmptyElement().toDTO(recipeId)

            database
                .from(IngredientTable)
                .upsert(upload, onConflict = IngredientUpsertField)
        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeIngredientList.\nmessage -> ${e.message}")
        }
    }

    override suspend fun uploadRecipeStepInfoList(recipeId: String, stepInfoList: List<StepInfo>) {
        val bucket = database
            .storage
            .from("${RecipeImageTable}/$recipeId/${StepInfoImageTable}")

        val upload = stepInfoList
            .removeEmptyElement()
            .mapIndexed { order, stepInfo->
                var imagePath = ""
                if(stepInfo.imageUri != Uri.EMPTY){
                    imagePath = stepInfo.imageUri.createStepInfoImagePath(order = order, context = context)
                    try{

                        bucket.upload(uri = stepInfo.imageUri, path = imagePath, upsert = true)
                    }
                    catch (e: Exception){
                        throw IOException("IOException in uploadRecipeStepInfoList. - step info image\nmessage -> ${e.message}")
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
            throw IOException("IOException in uploadRecipeStepInfoList.\nmessage -> ${e.message}")
        }
    }

    override suspend fun uploadRecipeThumbnail(recipeId: String, thumbnailUri: Uri) {
        try {
            val imagePath = thumbnailUri
                .createThumbnailImagePath(recipeId = recipeId, context = context)

            database
                .storage
                .from("${RecipeImageTable}/$recipeId")
                .upload(uri = thumbnailUri, path = imagePath, upsert = true)
        }
        catch (e: Exception){
            throw IOException("IOException in uploadRecipeIngredientList.\nmessage -> ${e.message}")
        }
    }
}