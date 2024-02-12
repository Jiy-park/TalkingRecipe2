package com.dd2d.talkingrecipe2.view.sub_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.ui.CommonValue
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun FriendListView(
    friendList: List<SimpleUserInfo>,
    onCLickFriend: (friendInfo: SimpleUserInfo) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(15.dp),
        modifier = modifier.fillMaxSize()
    ){
        items(
            items = friendList,
            key = { friend-> friend.userId }
        ){friend->
            FriendViewer(
                friend = friend,
                onClickFriend = { friendInfo-> onCLickFriend(friendInfo) },
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}

/** @param alsoMyFriend 다른 유저의 친구 목록에 본인 친구가 존재하는 경우 true. true의 경우 이름 옆에 '친구' 표식 뜸.*/
@Composable
fun FriendViewer(
    modifier: Modifier = Modifier,
    friend: SimpleUserInfo,
    alsoMyFriend: Boolean = true,
    onClickFriend: (friendInfo: SimpleUserInfo) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(CommonValue.FriendViewerHeight)
            .clickableWithoutRipple { onClickFriend(friend) }
    ) {
        val innerModifier = Modifier
        AsyncImage(
            model = friend.userProfileImageUri,
            contentDescription = "friend user profile image",
            contentScale = ContentScale.Crop,
            modifier = innerModifier
                .fillMaxHeight()
                .aspectRatio(1F / 1F)
                .graphicsLayer {
                    shape = CircleShape
                    clip = true
                }
        )

        kotex(text = "${friend.userName} @${friend.userId}")

        if(alsoMyFriend){
            kotex(text = "친구", color = MainColor)
        }
    }
}