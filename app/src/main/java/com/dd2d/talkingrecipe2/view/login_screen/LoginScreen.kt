package com.dd2d.talkingrecipe2.view.login_screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGradient
import com.dd2d.talkingrecipe2.view.login_screen.LoginState.None
import com.dd2d.talkingrecipe2.view.login_screen.LoginState.OnJoin
import com.dd2d.talkingrecipe2.view.login_screen.LoginState.OnLogin
import com.dd2d.talkingrecipe2.view.login_screen.sliding_window_item.JoinWindowView
import com.dd2d.talkingrecipe2.view.login_screen.sliding_window_item.LoginWindowView


/** 로그인 화면에서의 상태. 로그인 화면에서만 쓰임.
 *- [None]
 *- [OnLogin]
 *- [OnJoin] */
private sealed class LoginState {
    /** 아무 상태도 아님. 타이틀과 버튼만 보이는 상태.*/
    object None: LoginState()
    /** 로그인 상태. 로그인 창이 올라온 상태.*/
    object OnLogin: LoginState()
    /** 회원가입 상테. 회원가입 창이 올라온 상태.*/
    object OnJoin: LoginState()
}

/** @param checkDuplicateUserId 파라미터로 받은 값( = userId )이 중복된 값인지 확인. 중복된 값인 경우 true, 사용가능한 경우 false 반환. 판단중인 경우 null.
 * @param tryLogin 유저가 입력한 값으로 로그인 시도. 로그인 성공 시 [Screen.Main]으로 이동. 실패 시 알림.
 * @param joinNewUser 유저가 새로 회원 가입함. */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    checkDuplicateUserId: suspend (userId: String) -> Boolean,
    tryLogin: suspend (userId: String, userPassword: String) -> Boolean,
    joinNewUser: (userId: String, userPassword: String, userName: String) -> Unit,
){
    /** 로그인 화면에서의 상태.
     * @see LoginState*/
    var loginState by remember { mutableStateOf<LoginState>(None) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(brush = BackgroundGradient)
    ){
        val windowHeight by animateDpAsState(
            targetValue = if (loginState != None) maxHeight*0.9F else 0.dp,
            label = ""
        )
        val windowTitle = when(loginState){
            is None -> { "" }
            is OnLogin -> { "로그인" }
            is OnJoin -> { "회원가입" }
        }

        LoginScreenTitle(modifier = modifier.align(Alignment.Center))
        LoginScreenButton(
            onClickLogin = { loginState = OnLogin },
            onClickJoin = { loginState = OnJoin },
            clickable = loginState == None,
            modifier = modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
        )
        LoginScreenSlidingWindow(
            windowTitle = windowTitle,
            onClickClose = { loginState = None },
            modifier = modifier
                .align(Alignment.BottomCenter)
                .height(windowHeight)
        ){ contentModifier ->
            when(loginState){
                is None -> {  }
                is OnLogin -> {
                    LoginWindowView(
                        modifier = contentModifier,
                        onClickJoin = { loginState = OnJoin },
                        tryLogin = { userId, userPassword->
                            tryLogin(userId, userPassword)
                        }
                    )
                }
                is OnJoin -> {
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


