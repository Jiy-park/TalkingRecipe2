package com.dd2d.talkingrecipe2.view.recipe_read_screen.talking_recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import com.dd2d.talkingrecipe2.ui.CommonValue
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGradient
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun TalkingRecipeStepMoveButton(
    modifier: Modifier = Modifier,
    currentStep: Int,
    lastStep: Int,
    onClickNext: ()->Unit,
    onClickPrev: () -> Unit
){
    val innerModifier = Modifier
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(CommonValue.BottomButtonHeight)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = innerModifier
                .fillMaxHeight()
                .weight(1F)
                .background(brush = BackgroundGradient, shape = RectangleShape)
                .clickable { onClickPrev() }
        ){
            val text = if(currentStep == 0) "레시피 다시보기" else "이전"
            kotex(text = text, color = Color.White, weight = FontWeight.Bold)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = innerModifier
                .fillMaxHeight()
                .weight(1F)
                .background(brush = BackgroundGradient, shape = RectangleShape)
                .clickable { onClickNext() }
        ){
            val text = if(currentStep == lastStep-1) "메인으로" else "다음"
            kotex(text = text, color = Color.White, weight = FontWeight.Bold)
        }
    }
}
