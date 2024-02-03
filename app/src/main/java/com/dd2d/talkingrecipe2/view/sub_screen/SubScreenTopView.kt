package com.dd2d.talkingrecipe2.view.sub_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.view.sub_screen.top_view_item.EditUserInfoView
import com.dd2d.talkingrecipe2.view.sub_screen.top_view_item.UserInfoView

@Composable
fun SubScreenTopView(
    userInfo: User,
    onEditUserInfo: (update: User)->Unit,
    onClickBack: ()->Unit,
){
    var onEdit by remember { mutableStateOf(false) }
    AnimatedContent(
        targetState = onEdit, label = "",
        transitionSpec = {
            if(onEdit){
                slideInHorizontally { it*2 } + fadeIn() togetherWith
                        slideOutHorizontally { -it*2 } + fadeOut()

            }
            else{
                slideInHorizontally { it*2 } + fadeIn() togetherWith
                        slideOutHorizontally { -it*2 } + fadeOut()
            }
        },
    ) {isEdit->
        if(isEdit){
            EditUserInfoView(
                userInfo = userInfo,
                onCancelEdit = { onEdit = false },
                onEndEdit = { update->
                    onEdit = false
                    onEditUserInfo(update)
                }
            )
        }
        else{
            UserInfoView(
                userInfo = userInfo,
                onClickEditUserInfo = { onEdit = true },
                onClickBack = { onClickBack() },
            )
        }
    }
}
