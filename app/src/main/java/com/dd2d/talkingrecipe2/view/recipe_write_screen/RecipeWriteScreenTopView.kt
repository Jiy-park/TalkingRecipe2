package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.runtime.Composable
import com.dd2d.talkingrecipe2.navigation.CreateScreenMode
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.view.TopView

@Composable
fun RecipeWriteScreenTopView(
    visible: Boolean,
    createScreenMode: CreateScreenMode,
    onClickBack: ()->Unit
){
    if(visible){
        val title = when(createScreenMode){
            is CreateScreenMode.Create -> { "레시피 만들기" }
            is CreateScreenMode.Modify -> { "레시피 수정하기" }
        }
        TopView(
            text = title,
            fontColor = MainText,
            textLeftImageRes = null,
            onClickBack = { onClickBack() },
        )
//        Box(
//            modifier = modifier
//                .fillMaxWidth()
//                .height(TopViewHeight)
//                .background(color = Color.Transparent)
//        ) {
//            IconButton(
//                onClick = { onClickBack() },
//                modifier = modifier
//                    .align(Alignment.CenterStart)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBack,
//                    tint = MainText,
//                    contentDescription = "back button"
//                )
//            }
//
//            Text(
//                text = title,
//                fontFamily = MapleFontFamily,
//                fontWeight = FontWeight.Bold,
//                color = MainText,
//                fontSize = 20.sp,
//                modifier = modifier.align(Alignment.Center)
//            )
//        }
    }
}