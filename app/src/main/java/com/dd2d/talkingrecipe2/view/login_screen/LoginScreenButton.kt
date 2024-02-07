package com.dd2d.talkingrecipe2.view.login_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun LoginScreenButton(
    modifier: Modifier = Modifier,
    onClickLogin: ()->Unit,
    onClickJoin: ()->Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ){
        val innerModifier = Modifier
        ElevatedButton(
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color.White
            ),
            contentPadding = PaddingValues(vertical = 15.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 5.dp
            ),
            shape = RoundedCornerShape(40.dp),
            onClick = { onClickLogin() },
            modifier = innerModifier.fillMaxWidth(0.8F)
        ) {
            kotex(text = "로그인")
        }
        kotex(
            text = "1분만에 회원가입",
            size = 13.sp,
            color = Color.White,
            modifier = innerModifier.clickableWithoutRipple {
                onClickJoin()
            }
        )
    }
}