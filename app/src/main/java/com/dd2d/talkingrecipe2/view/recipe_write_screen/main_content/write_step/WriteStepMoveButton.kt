package com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.write_step

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateStep
import com.dd2d.talkingrecipe2.ui.CommonValue.BottomButtonHeight
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGradient
import com.dd2d.talkingrecipe2.ui.theme.kotex

/** 레시피 만들기에서 다음 단계 혹은 이전 단계로 이동하기 위한 버튼.
 * @param onClickNextStep 다음 단계로 넘거감. null일 경우 다음 단계가 없는 것.
 * @param onClickPrevStep 이전 단계로 넘어감. null일 경우 이전 단계가 없는 것.*/
@Composable
fun WriteStepMoveButton(
    modifier: Modifier = Modifier,
    createStep: CreateStep,
    visible: Boolean,
    onClickNextStep: ()->Unit,
    onClickPrevStep: ()->Unit,
){
    if(visible){
        val innerModifier = Modifier
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.height(BottomButtonHeight)
        ) {
            if(createStep.step > CreateStep.values().first().step){
                Button(
                    onClick = { onClickPrevStep() },
                    shape = RectangleShape,
                    colors = buttonColor(),
                    modifier = innerModifier
                        .background(brush = BackgroundGradient)
                        .weight(1F)
                ) {
                    kotex(
                        text = "이전",
                        color = Color.White,
                        weight = FontWeight.Bold
                    )
                }
            }
            if(createStep.step < CreateStep.values().last().step){
                Button(
                    onClick = { onClickNextStep() },
                    shape = RectangleShape,
                    colors = buttonColor(),
                    modifier = innerModifier
                        .background(brush = BackgroundGradient)
                        .weight(1F)
                ) {
                    kotex(
                        text = if(createStep.step == CreateStep.values().lastIndex-1) "완료" else "다음",
                        color = Color.White,
                        weight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/** 레시피 만들기에서 다음 단계 혹은 이전 단계로 이동할 때 쓰이는 버튼의 색상*/
@Composable
private fun buttonColor() = ButtonDefaults
    .buttonColors(
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = Color.Transparent
    )