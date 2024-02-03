package com.dd2d.talkingrecipe2.view.sub_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dd2d.talkingrecipe2.data_struct.FriendInfo
import com.dd2d.talkingrecipe2.data_struct.RecipePost
import com.dd2d.talkingrecipe2.llog
import com.dd2d.talkingrecipe2.logging
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingFriendList
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingPostList
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingUser

/**@param destination 서브 메인의 시작점. 참고 : [SubScreenDestination] */
@Composable
@Preview(showSystemUi = true)
fun SubScreen(
    modifier: Modifier = Modifier,
    destination: SubScreenDestination = SubScreenDestination.MyPost,
    onClickPost: (RecipePost) -> Unit = { logging("click post") },
    onClickFriend: (FriendInfo) -> Unit = { logging("click friend") },
    onClickBack: () -> Unit = llog("click back"),
){
    var currentTab by remember { mutableStateOf<SubScreenDestination>(destination) }
//    TODO("아래 세 줄은 테스트용 나중에 지우면 됨. user 정보는 뷰모델에서 가져올 것.")
    var user by remember { mutableStateOf(TestingUser) }
    val postList by remember { mutableStateOf(TestingPostList) }
    val friendList by remember { mutableStateOf(TestingFriendList) }

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
                    PostListView(
                        postList = postList,
                        onClickPost = { post -> onClickPost(post) },
                        modifier = modifier
                    )
                }
                SubScreenDestination.Friend -> {
                    FriendListView(
                        friendList = friendList,
                        onCLickFriend = { friendInfo-> onClickFriend(friendInfo) },
                        modifier = modifier
                    )
                }
                SubScreenDestination.SavePost -> {
                    PostListView(
                        postList = postList,
                        onClickPost = { post -> onClickPost(post) },
                        modifier = modifier
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