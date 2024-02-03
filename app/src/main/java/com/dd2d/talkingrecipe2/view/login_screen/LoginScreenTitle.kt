package com.dd2d.talkingrecipe2.view.login_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.theme.matex

@Composable
fun LoginScreenTitle(modifier: Modifier = Modifier){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.outline_toxi_head),
            contentDescription = null,
            modifier = modifier.graphicsLayer {
                scaleX = 1.5F
                scaleY = 1.5F
            }
        )
        Spacer(modifier = modifier.width(15.dp))
        matex(text = "토킹레시피", color = Color.White, size = 40.sp)
    }
}