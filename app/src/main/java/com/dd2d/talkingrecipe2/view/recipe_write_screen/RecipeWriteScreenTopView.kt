package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.runtime.Composable
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteMode
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.view.TopView

@Composable
fun RecipeWriteScreenTopView(
    createScreenMode: RecipeWriteMode,
    onClickBack: ()->Unit
){
    val title = when(createScreenMode){
        is RecipeWriteMode.Create -> { "레시피 만들기" }
        is RecipeWriteMode.Modify -> { "레시피 수정하기" }
    }
    TopView(
        text = title,
        fontColor = MainText,
        textLeftImageRes = null,
        onClickBack = { onClickBack() },
    )

}