package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.runtime.Composable
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.view.TopView
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode

/** 레시지 작성 또는 수정 화면에서의 상단 영역.
 * @param requestErrorView [recipeMode]의 값이 예상치 못한 값인 경우 호출.*/
@Composable
fun RecipeWriteScreenTopView(
    recipeMode: RecipeViewModelMode,
    onClickBack: ()->Unit,
    requestErrorView: ()->Unit
){
    val title = when(recipeMode){
        is RecipeViewModelMode.WriteMode -> { "레시피 만들기" }
        is RecipeViewModelMode.ModifyMode -> { "레시피 수정하기" }
        else -> {
            requestErrorView()
            ""
        }
    }
    TopView(
        text = title,
        fontColor = MainText,
        textLeftImageRes = null,
        onClickBack = { onClickBack() },
    )

}