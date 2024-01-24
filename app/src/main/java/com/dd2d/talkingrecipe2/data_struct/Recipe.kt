package com.dd2d.talkingrecipe2.data_struct

import android.net.Uri
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import kotlinx.serialization.Serializable

data class Recipe(
    var basicInfo: RecipeBasicInfo = RecipeBasicInfo(),
    var ingredientList: MutableList<Ingredient> = mutableListOf(),
    var stepInfoList: MutableList<StepInfo> = mutableListOf(),
    var thumbnailUri: Uri = Uri.EMPTY
)