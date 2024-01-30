package com.dd2d.talkingrecipe2.data_struct.recipe_write

/** 레시피 쓰기 작업에 대한 분류
 * @property Create
 * @property Modify*/
sealed class RecipeWriteMode{
    /** 새로운 레시피를 만듦. */
    object Create: RecipeWriteMode()

    /** 기존의 레시피를 수정함.
     * @param recipeId 수정할 레시피의 아이디.*/
    class Modify(val recipeId: String): RecipeWriteMode()
}