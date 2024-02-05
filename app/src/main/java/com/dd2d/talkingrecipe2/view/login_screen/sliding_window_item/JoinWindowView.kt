package com.dd2d.talkingrecipe2.view.login_screen.sliding_window_item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGradient
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.PassColor
import com.dd2d.talkingrecipe2.ui.theme.kofield
import com.dd2d.talkingrecipe2.ui.theme.kotex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** @param checkDuplicateUserId 유저가 입력한 아이디가 중복된 값인지 확인. 중복된 값일 경우 true. 아닐 경우 false 반납. 판단 중일 경우 null.
 * @param joinNewUser 유저가 입력한 값으로 새롭게 회원 가입. 해당 함수를 호출 전에 입력한 userId의 값이 사용가능한 값인지 확인 후, 해당 함수가 호출되어야 함.*/
@Composable
fun JoinWindowView(
    modifier: Modifier,
    checkDuplicateUserId: suspend (userId: String) -> Boolean,
    joinNewUser: (userId: String, userPassword: String, userName: String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var userId by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userPasswordCheck by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    var userIdDuplicationError by remember { mutableStateOf<Boolean?>(null) }
    var userPasswordCheckError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier.fillMaxSize()
    ){
        kofield(
            value = userId,
            onValueChange = { userId = it },
            placeholder = { kotex(text = "아이디") },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, tint = HintText, contentDescription = "") },
            trailingIcon = {
                IconButton(
                    enabled = userId.isNotBlank(),
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            userIdDuplicationError = checkDuplicateUserId(userId)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        tint = userIdDuplicationError?.let { isDuplicate-> if(isDuplicate) MainColor else PassColor }?: HintText,
                        contentDescription = "check user id duplicate"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = userIdDuplicationError == true,
            supportingText = {
                userIdDuplicationError?.let { isDuplicate->
                    if(isDuplicate){
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                            modifier = modifier.fillMaxWidth()
                        ) {
                            kotex(text = "이미 사용중인 아이디입니다.", color = MainColor, size = 13.sp)
                            Icon(imageVector = Icons.Default.Warning, tint = MainColor, contentDescription = "")
                        }
                    }
                    else{
                        if(userId.isNotBlank()){
                            kotex(text = "사용 가능한 아이디입니다.", color = PassColor, size = 13.sp)
                        }
                        else{
                            userIdDuplicationError = null
                        }
                    }
                }
            },
            modifier = modifier.fillMaxWidth()
        )

        kofield(
            value = userPassword,
            onValueChange = { userPassword = it },
            placeholder = { kotex(text = "비밀번호") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, tint = HintText, contentDescription = "") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            visualTransformation = PasswordVisualTransformation(),
            isError = userPasswordCheckError,
            modifier = modifier.fillMaxWidth()
        )

        kofield(
            value = userPasswordCheck,
            onValueChange = { userPasswordCheck = it },
            placeholder = { kotex(text = "비밀번호 확인") },
            leadingIcon = { Icon(imageVector = Icons.Default.Check, tint = HintText, contentDescription = "") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            visualTransformation = PasswordVisualTransformation(),
            isError = userPasswordCheckError,
            supportingText = {
                if(userPasswordCheckError){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        kotex(text = "비밀번호가 일치하지 않습니다.", color = MainColor, size = 13.sp)
                        Icon(imageVector = Icons.Default.Warning, tint = MainColor, contentDescription = "")
                    }
                }
            },
            modifier = modifier.fillMaxWidth()
        )

        kofield(
            value = userName,
            onValueChange = { userName = it },
            placeholder = { kotex(text = "닉네임") },
            leadingIcon = { Icon(painter = painterResource(id = R.drawable.toxi_head_hint_color), tint = HintText, contentDescription = "") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            supportingText = { kotex(text = "닉네임은 언제든지 변경할 수 있습니다.", color = HintText, size = 13.sp) },
            modifier = modifier.fillMaxWidth()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
        ){
            ElevatedButton(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(vertical = 15.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 1.dp
                ),
                enabled = userId.isNotBlank() &&                // 유저 아이디가 비어있지 않음.
                        userPassword.isNotBlank() &&            // 유저 비밀번호가 비어있지 않음.
                        userName.isNotBlank() &&                // 유저 이름이 비어있지 않음.
                        userIdDuplicationError == false &&      // 유저 아이디가 중복되지 않음.
                        !userPasswordCheckError,                // 유저 비밀번호 체크가 돼 있음.
                onClick = {
//                    TODO("유저가 중복체크에서 사용가능 판정을 받은 후 다시 값을 바꾸면 이전의 값으로 인식되어 중복된 아이디로 로그인 가능해짐. 이거 해결해야함.")
                    joinNewUser(userId, userPassword, userName)
                },
                modifier = modifier
                    .fillMaxWidth(0.8F)
                    .background(brush = BackgroundGradient, shape = RoundedCornerShape(40.dp))
            ) {
                kotex(text = "가입완료", color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}