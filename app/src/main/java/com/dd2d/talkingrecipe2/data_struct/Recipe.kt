package com.dd2d.talkingrecipe2.data_struct

import android.net.Uri
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo

data class Recipe(
    var basicInfo: RecipeBasicInfo = RecipeBasicInfo(),
    var ingredientList: List<Ingredient> = mutableListOf(),
    var stepInfoList: List<StepInfo> = mutableListOf(),
    var thumbnailUri: Uri = Uri.EMPTY
){
    constructor(recipeId: String) : this() {
        Recipe(
            basicInfo = RecipeBasicInfo(recipeId = recipeId),
            ingredientList = mutableListOf(),
            stepInfoList = mutableListOf(),
            thumbnailUri = Uri.EMPTY
        )
    }
}