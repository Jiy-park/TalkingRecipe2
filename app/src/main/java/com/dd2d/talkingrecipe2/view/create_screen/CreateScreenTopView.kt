package com.dd2d.talkingrecipe2.view.create_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.navigation.CreateScreenMode
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.ui.theme.MapleFontFamily

@Composable
fun CreateScreenTopView(
    modifier: Modifier = Modifier,
    createScreenMode: CreateScreenMode,
    onClickBack: ()->Unit
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = Color.Transparent)
    ) {
        IconButton(
            onClick = { onClickBack() },
            modifier = modifier
                .align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                tint = MainText,
                contentDescription = "back button"
            )
        }

        Text(
            text = if(createScreenMode == CreateScreenMode.Create)"레시피 만들기" else "레시피 수정하기",
            fontFamily = MapleFontFamily,
            fontWeight = FontWeight.Bold,
            color = MainText,
            fontSize = 20.sp,
            modifier = modifier.align(Alignment.Center)
        )
    }
}