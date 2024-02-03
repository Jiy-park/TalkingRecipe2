package com.dd2d.talkingrecipe2.view.sub_screen.top_view_item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.ui.theme.SubColor
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.view.sub_screen.SubScreenValue

@Composable
fun UserInfoView(
    modifier: Modifier = Modifier,
    userInfo: User,
    onClickEditUserInfo: ()->Unit,
    onClickBack: ()->Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(SubScreenValue.SubScreenTopViewHeightRatio)
            .background(color = Color.White)
    ){
        IconButton(
            onClick = { onClickBack() },
            modifier = modifier
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                tint = MainText,
                contentDescription = "back button"
            )
        }

        /** 배경 이미지*/
        AsyncImage(
            model = userInfo.backgroundImageUri,
            contentDescription = "user background image",
            contentScale = ContentScale.FillBounds,
            modifier = modifier
                .fillMaxSize()
                .zIndex(-1F)
        )

        /** 프로필 이미지*/
        Surface(
            color = Color.LightGray,
            shape = CircleShape,
            border = BorderStroke(color = SubColor, width = 2.dp),
            modifier = modifier
                .align(Alignment.Center)
                .zIndex(1F)
                .size(120.dp)
        ) {
            AsyncImage(
                model = userInfo.profileImageUri,
                contentDescription = "user profile image",
                contentScale = ContentScale.Crop,
            )
        }

        Box(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.5F)
                .background(color = Color.White)
                .drawBehind {
                    drawLine(
                        color = SubColor,
                        strokeWidth = 2.dp.toPx(),
                        start = Offset(0F, 0F),
                        end = Offset(size.width, 0F)
                    )
                    drawLine(
                        color = SubColor,
                        strokeWidth = 2.dp.toPx(),
                        start = Offset(0F, size.height),
                        end = Offset(size.width, size.height)
                    )
                }
        ){
            val innerModifier = Modifier
            IconButton(
                onClick = { onClickEditUserInfo() },
                modifier = innerModifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    tint = HintText,
                    contentDescription = "edit user info"
                )
            }

            kotex(
                text = userInfo.userFullName,
                weight = FontWeight.Bold,
                modifier = modifier
                    .align(Alignment.Center)
                    .offset(y = 20.dp)
            )
        }
    }
}