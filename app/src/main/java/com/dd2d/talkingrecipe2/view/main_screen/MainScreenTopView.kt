package com.dd2d.talkingrecipe2.view.main_screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.view.TopView

@Composable
fun MainScreenTopView(){
    TopView(
        text = "토킹레시피",
        fontColor = Color.White,
        textLeftImageRes = R.drawable.outline_toxi_head,
        onClickBack = null,
        paddingValue = PaddingValues(horizontal = 15.dp),
        textAlignment = Alignment.CenterStart
    )
}