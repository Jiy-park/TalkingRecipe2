package com.dd2d.talkingrecipe2.view.login_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun LoginScreenSlidingWindow(
    modifier: Modifier = Modifier,
    windowTitle: String,
    onClickClose: ()->Unit,
    content: @Composable (contentModifier: Modifier)->Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxWidth()
    ){
        val innerModifier = Modifier
        Image(painter = painterResource(id = R.drawable.login), contentDescription = null)
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = innerModifier
                .fillMaxSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(30.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = innerModifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                kotex(text = windowTitle, size = 18.sp, weight = FontWeight.Bold)
                IconButton(onClick = { onClickClose() }) {
                    Icon(imageVector = Icons.Default.Close, tint = MainText, contentDescription = "close window")
                }
            }
            content(innerModifier)
        }
    }
}