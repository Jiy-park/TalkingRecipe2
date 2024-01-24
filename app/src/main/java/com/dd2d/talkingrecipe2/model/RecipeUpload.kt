package com.dd2d.talkingrecipe2.model

import android.net.Uri
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo

interface RecipeUpload {
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