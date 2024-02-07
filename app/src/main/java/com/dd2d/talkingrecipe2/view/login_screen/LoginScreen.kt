package com.dd2d.talkingrecipe2.view.login_screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGradient
import com.dd2d.talkingrecipe2.view.login_screen.LoginScreenSlider.Down
import com.dd2d.talkingrecipe2.view.login_screen.LoginScreenSlider.UpJoin
import com.dd2d.talkingrecipe2.view.login_screen.LoginScreenSlider.UpLogin
import com.dd2d.talkingrecipe2.view.login_screen.sliding_window_item.JoinWindowView
import com.dd2d.talkingrecipe2.view.login_screen.sliding_window_item.LoginWindowView
import com.dd2d.talkingrecipe2.view_model.LoginState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/** [LoginScreen]의 슬라이딩 윈도우의 상태.
 * 윈도우 간 전환 시 기존의 윈도우는 내려간 후에 새로운 윈도우가 올라가야 한다.
 *- [UpLogin]
 *- [UpJoin]
 *- [Down]
 * */
private enum class LoginScreenSlider(val title: String){
    /** 로그인 슬라이딩 윈도우가 올라옴.*/
    UpLogin("로그인"),
    /** 회원 가입 슬라이딩 윈도우가 올라옴.*/
    UpJoin("회원가입"),
    /** 어떠한 것도 올라와 있지 않음.*/
    Down("")
}

/** @param checkDuplicateUserId 파라미터로 받은 값( = userId )이 중복된 값인지 확인. 중복된 값인 경우 true, 사용가능한 경우 false 반환.
 * @param tryLogin 유저가 입력한 값으로 로그인 시도. 로그인 성공 시 [Screen.Main]으로 이동. 실패 시 알림.
 * @param joinNewUser 유저가 새로 회원 가입함. */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginState: LoginState,
    checkDuplicateUserId: suspend (userId: String) -> Boolean,
    tryLogin: suspend (userId: String, userPassword: String) -> Boolean,
    joinNewUser: (userId: String, userPassword: String, userName: String) -> Unit,
){
    /** 로그인 화면에서의 상태.
     * @see LoginState*/
    val scope = rememberCoroutineScope()
    var window by remember { mutableStateOf(Down) }
    val windowHeightRatio by animateFloatAsState(
        targetValue = if(window == Down) 0F else 0.9F,
        label = ""
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = BackgroundGradient)
    ){
        LoginScreenTitle(modifier = modifier.align(Alignment.Center))
        LoginScreenButton(
            onClickLogin = { window = UpLogin },
            onClickJoin = { window = UpJoin },
            modifier = modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
        )
        LoginScreenSlidingWindow(
            windowTitle = window.title,
            onClickClose = { window = Down },
            windowHeightRatio = windowHeightRatio,
            modifier = modifier .align(Alignment.BottomCenter)
        ){ contentModifier ->
            when(window){
                Down -> {  }
                UpLogin -> {
                    LoginWindowView(
                        modifier = contentModifier,
                        onClickJoin = {
                            scope.launch {
                                window = Down
                                delay(300L)
                                window = UpJoin
                            }
                        },
                        tryLogin = { userId, userPassword->
                            tryLogin(userId, userPassword)
                        }
                    )
                }
                UpJoin -> {
                    JoinWindowView(
                        modifier = contentModifier,
                        checkDuplicateUserId = { userId ->
                            checkDuplicateUserId(userId)
                        },
                        joinNewUser = { userId, userPassword, userName ->
                            joinNewUser(userId, userPassword, userName)
                        }
                    )
                }
            }
        }
    }
}




