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
import com.dd2d.talkingrecipe2.data_struct.RecipePost
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination

/**@param destination 서브 메인의 시작점. 참고 : [SubScreenDestination] */
@Composable
fun SubScreen(
    modifier: Modifier = Modifier,
    user: User,
    myPostList: List<RecipePost>,
    friendList: List<SimpleUserInfo>,
    savePostList: List<RecipePost>,
    onUpdateUser: (update: User)->Unit,
    destination: SubScreenDestination,
    onClickPost: (RecipePost) -> Unit,
    onClickFriend: (SimpleUserInfo) -> Unit,
    onClickBack: () -> Unit,
){
    var currentTab by remember { mutableStateOf<SubScreenDestination>(destination) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
    ){
        SubScreenTopView(
            userInfo = user,
            onEditUserInfo = { update-> onUpdateUser(update) },
            onClickBack = { onClickBack() }
        )
        SubScreenCenterView(
            currentTab = currentTab.ordinal,
            onTabChange = { tabIndex-> currentTab = SubScreenDestination.values()[tabIndex] }
        ){
            when(currentTab){
                SubScreenDestination.MyPost -> {
                    PostListView(
                        postList = myPostList,
                        emptyComment = "작성한 레시피가 없어요.",
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
                        postList = savePostList,
                        emptyComment = "보관중인 레시피가 없어요.",
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