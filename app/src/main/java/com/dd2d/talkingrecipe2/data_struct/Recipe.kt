package com.dd2d.talkingrecipe2.data_struct

import android.net.Uri
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo


data class Recipe(
    var basicInfo: RecipeBasicInfo,
    var ingredientList: List<Ingredient>,
    var stepInfoList: List<StepInfo>,
    var thumbnailUri: Uri
){
    companion object{
        /** 어떠한 내용이 없는 레시피. */
        val EmptyRecipe = Recipe(
            basicInfo = RecipeBasicInfo(),
            ingredientList = emptyList(),
            stepInfoList = emptyList(),
            thumbnailUri = Uri.EMPTY,
        )
    }
}