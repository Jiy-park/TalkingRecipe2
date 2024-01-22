package com.dd2d.talkingrecipe2.data_struct.recipe_create

enum class CreateStep(val step: Int) {
    RecipeBasicInfo(step = 0),
    RecipeStepInfo(step = 1),
    RecipeThumbnail(step = 2),
    EndCreate(step = 3),
}