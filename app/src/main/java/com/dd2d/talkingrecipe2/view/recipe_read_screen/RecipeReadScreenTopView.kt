package com.dd2d.talkingrecipe2.view.recipe_read_screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.view.TopView

@Composable
fun RecipeReadScreenTopView(modifier: Modifier = Modifier, onClickBack: () -> Unit){
    TopView(
        text = "토킹레시피",
        fontColor = MainText,
        textLeftImageRes = R.drawable.outline_toxi_head_main_text_color,
        onClickBack = { onClickBack() },
        modifier = modifier
    )
}