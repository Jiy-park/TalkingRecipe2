package com.dd2d.talkingrecipe2.data_struct.recipe_write

enum class RecipeWriteStep(val step: Int){
    RecipeBasicInfo(step = 0),
    RecipeStepInfo(step = 1),
    RecipeThumbnail(step = 2),
}