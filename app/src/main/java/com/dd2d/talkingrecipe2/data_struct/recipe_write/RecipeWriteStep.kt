package com.dd2d.talkingrecipe2.data_struct.recipe_write

import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteStep.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteStep.RecipeStepInfo
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteStep.RecipeThumbnail

/** 레시피 만들기 단계.
 *- [RecipeBasicInfo]
 *- [RecipeStepInfo]
 *- [RecipeThumbnail]*/
enum class RecipeWriteStep{
    /** 레시피의 기본적인 정보와 재료 정보를 입력하는 단계.*/
    RecipeBasicInfo,
    /** 레시피의 조리 순서를 입력하는 단계.*/
    RecipeStepInfo,
    /** 레시피의 썸네일과 공유 옵션을 선택하는 단계.*/
    RecipeThumbnail,
}