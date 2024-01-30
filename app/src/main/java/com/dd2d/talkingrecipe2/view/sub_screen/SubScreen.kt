package com.dd2d.talkingrecipe2.view.sub_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dd2d.talkingrecipe2.llog
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingUser

/**
 * @param destination 서브 메인의 시작점. 참고 : [SubScreenDestination] */
@Composable
@Preview(showSystemUi = true)
fun SubScreen(
    modifier: Modifier = Modifier,
    destination: String = SubScreenDestination.MyPost.route,
    onClickBack: () -> Unit = llog("click back"),
){

//    TODO("아래 한 줄은 테스트용 나중에 지우면 됨. user 정보는 뷰모델에서 가져올 것.")
    var user by remember { mutableStateOf(TestingUser) }

    SubScreenTopView(
        userInfo = user,
        onEditUserInfo = { update-> user = update },
        onClickBack = { onClickBack() }
    )
}

object SubScreenValue {
    /** [Screen.Sub]의 최산단 뷰의 높이 비율. */
    const val SubScreenTopViewHeightRatio = 0.35F
}