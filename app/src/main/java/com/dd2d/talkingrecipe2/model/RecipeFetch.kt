package com.dd2d.talkingrecipe2.model

import android.net.Uri
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo

interface RecipeFetch {
    suspend fun fetRecipeById(recipeId: String, onChangeFetchingState: (msg: String) -> Unit): Recipe
    suspend fun fetchRecipeBasicInfoById(recipeId: String): RecipeBasicInfo
    suspend fun fetchRecipeIngredientListById(recipeId: String): List<Ingredient>
    suspend fun fetchRecipeStepInfoListById(recipeId: String): List<StepInfo>
    suspend fun fetchRecipeThumbnailUriById(recipeId: String): Uri
}
