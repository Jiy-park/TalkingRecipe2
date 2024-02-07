package com.dd2d.talkingrecipe2.view.login_screen.sliding_window_item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGradient
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.kofield
import com.dd2d.talkingrecipe2.ui.theme.kotex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** @param tryLogin 유저가 입력한 값으로 로그인 시도. 로그인 성공 시 [Screen.Main]으로 이동. 실패 시 알림.  */
@Composable
fun LoginWindowView(
    modifier: Modifier,
    onClickJoin: ()->Unit,
    tryLogin: suspend (userId: String, userPassword: String)->Boolean
) {
    val scope = rememberCoroutineScope()

    /** 유저가 로그인 버튼을 클릭 시 [tryLogin] 함수 호출.
     * [tryLogin] 결과값이 false일 경우 [isFailLogin] 값이 true가 됨.*/
    var isFailLogin by remember { mutableStateOf(false) }

    var userId by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var visiblePassword by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .fillMaxSize()
    ){
        kofield(
            value = userId,
            onValueChange = { userId = it },
            isError = isFailLogin,
            placeholder = { kotex(text = "아이디", color = HintText) },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, tint = HintText, contentDescription = "") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = modifier.fillMaxWidth()
        )
        kofield(
            value = userPassword,
            onValueChange = { userPassword = it },
            isError = isFailLogin,
            placeholder = { kotex(text = "비밀번호", color = HintText) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, tint = HintText, contentDescription = "") },
            trailingIcon = {
                IconButton(onClick = { visiblePassword = !visiblePassword }) {
                    Icon(
                        painter = painterResource(
                            id = if(visiblePassword) R.drawable.ic_visible else R.drawable.ic_blind
                        ),
                        tint = HintText,
                        contentDescription = "",
                    )
                }
            },
            supportingText = {
                if(isFailLogin){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        kotex(text = "아이디 또는 비밀번호를 잘못 입력했습니다.", color = MainColor, size = 13.sp)
                        Icon(imageVector = Icons.Default.Warning, tint = MainColor, contentDescription = "")
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password,imeAction = ImeAction.Done),
            visualTransformation = if(visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = modifier.fillMaxWidth()
        )

        Spacer(modifier = modifier.height(200.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
                .fillMaxWidth()
        ){
            ElevatedButton(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(vertical = 15.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 1.dp
                ),
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        isFailLogin = !tryLogin(userId, userPassword)
                    }
                },
                modifier = modifier
                    .fillMaxWidth(0.8F)
                    .background(brush = BackgroundGradient, shape = RoundedCornerShape(40.dp))
            ) {
                kotex(text = "로그인", color = Color.White, weight = FontWeight.Bold)
            }
            kotex(
                text = "회원가입",
                size = 13.sp,
                modifier = modifier
                    .clickableWithoutRipple { onClickJoin() }
            )
        }
    }
}