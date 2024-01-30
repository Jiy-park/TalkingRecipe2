package com.dd2d.talkingrecipe2.view.sub_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dd2d.talkingrecipe2.llog
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingUser

/**@param destination 서브 메인의 시작점. 참고 : [SubScreenDestination] */
@Composable
@Preview(showSystemUi = true)
fun SubScreen(
    modifier: Modifier = Modifier,
    destination: SubScreenDestination = SubScreenDestination.MyPost,
    onClickBack: () -> Unit = llog("click back"),
){
    var currentTab by remember { mutableStateOf<SubScreenDestination>(destination) }
//    TODO("아래 한 줄은 테스트용 나중에 지우면 됨. user 정보는 뷰모델에서 가져올 것.")
    var user by remember { mutableStateOf(TestingUser) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
    ){
        SubScreenTopView(
            userInfo = user,
            onEditUserInfo = { update-> user = update },
            onClickBack = { onClickBack() }
        )
        SubScreenCenterView(
            currentTab = currentTab.ordinal,
            onTabChange = { tabIndex-> currentTab = SubScreenDestination.values()[tabIndex] }
        ){
            when(currentTab){
                SubScreenDestination.MyPost -> {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .background(color = Color.Cyan)
                    )
                }
                SubScreenDestination.Friend -> {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .background(color = Color.Green)
                    )
                }
                SubScreenDestination.SavePost -> {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .background(color = Color.Gray)
                    )
                }
            }
        }
    }
}

object SubScreenValue {
    /** [Screen.Sub]의 최산단 뷰의 높이 비율. */
    const val SubScreenTopViewHeightRatio = 0.35F
}