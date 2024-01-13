package com.dd2d.talkingrecipe2.view.main_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.ui.theme.kopupFontFamily

@Composable
fun MainScreenCenterView(
    modifier: Modifier = Modifier,
    onClickSearchTrigger: ()->Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .offset(y = 20.dp)
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .clickableWithoutRipple { onClickSearchTrigger() }
        ){
            Image(
                painter = painterResource(id = R.drawable.main_screen_search),
                contentDescription = "search trigger",
                modifier = modifier
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.padding(top = 10.dp)
            ){
                Text(text = "오늘은 어떤 요리를\n시작할까요?", fontFamily = kopupFontFamily, fontWeight = FontWeight.Medium, color = MainText, textAlign = TextAlign.Center, fontSize = 15.sp)
                Text(text = "터치해서 레시피를 찾아보세요!", fontFamily = kopupFontFamily, fontWeight = FontWeight.Light, color = HintText, textAlign = TextAlign.Center, fontSize = 13.sp)

            }
        }
        Image(
            painter = painterResource(id = R.drawable.main_screen_toxi),
            contentDescription = "main screen toxi"
        )
    }
}