package com.dd2d.talkingrecipe2.view.sub_screen.top_view_item

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.ui.theme.SubColor
import com.dd2d.talkingrecipe2.ui.theme.kofield
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.view.sub_screen.SubScreenValue

@Composable
fun EditUserInfoView(
    modifier: Modifier = Modifier,
    userInfo: User,
    onCancelEdit: ()->Unit,
    onEndEdit: (update: User)->Unit,
){
    var updatedUserInfo by remember { mutableStateOf(userInfo) }
    /** 변경할 사진이 프로필 이미지임. 프로필 이미지를 클릭 시 해당 값이 true로 변경됨. [ActivityResultContracts.GetContent]로 받아온 결과값(=[Uri]?)을 프로필 이미지에 적용. 적용 후엔 다시 false.*/
    var onEditProfile by remember { mutableStateOf(false) }
    /** 변경할 사진이 배경 이미지임. 배경 이미지를 클릭 시 해당 값이 true로 변경됨. [ActivityResultContracts.GetContent]로 받아온 결과값(=[Uri]?)을 배경 이미지에 적용. 적용 후엔 다시 false.*/
    var onEditBackground by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ){ res: Uri?->
        res?.let{ uri->
            if(onEditProfile){
                updatedUserInfo = updatedUserInfo.copy(profileImageUri = uri)
                onEditProfile = false
            }
            if(onEditBackground){
                updatedUserInfo = updatedUserInfo.copy(backgroundImageUri = uri)
                onEditBackground = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(SubScreenValue.SubScreenTopViewHeightRatio)
            .background(color = Color.White)
    ){
        IconButton(
            onClick = { onCancelEdit() },
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
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .zIndex(-1F)
                .clickableWithoutRipple {
                    onEditBackground = true
                    galleryLauncher.launch("image/*")
                }
        ){
            AsyncImage(
                model = updatedUserInfo.backgroundImageUri,
                contentDescription = "user background image",
                contentScale = ContentScale.FillBounds,
                alpha = 0.5F,
                modifier = modifier
                    .background(color = Color.Black.copy(alpha = 0.5F))
                    .matchParentSize()
            )
            Image(
                painter = painterResource(id = R.drawable.ic_photo),
                contentDescription = "",
                modifier = modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-20).dp, y = 40.dp)
            )
        }

        /** 프로필 이미지*/
        Surface(
            color = Color.LightGray,
            shape = CircleShape,
            border = BorderStroke(color = SubColor, width = 2.dp),
            modifier = modifier
                .align(Alignment.Center)
                .zIndex(1F)
                .size(120.dp)
                .clickableWithoutRipple {
                    onEditProfile = true
                    galleryLauncher.launch("image/*")
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
            ){
                AsyncImage(
                    model = updatedUserInfo.profileImageUri,
                    contentDescription = "user profile image",
                    contentScale = ContentScale.Crop,
                    alpha = 0.5F,
                    modifier = modifier
                        .background(color = Color.Black.copy(alpha = 0.5F))
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_photo),
                    contentDescription = "",
                )
            }
        }

        /** 유저의 이름*/
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = innerModifier
                    .align(Alignment.TopEnd)
                    .wrapContentSize()
                    .padding(top = 10.dp, end = 10.dp)
            ){
                OutlinedButton(
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(2.dp, color = MainColor),
                    onClick = { onCancelEdit() }
                ) {
                    kotex(text = "취소", color = MainColor)
                }
                OutlinedButton(
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(2.dp, color = MainColor),
                    onClick = { onEndEdit(updatedUserInfo) }
                ) {
                    kotex(text = "저장", color = MainColor)
                }
            }

            kofield(
                value = updatedUserInfo.name,
                onValueChange = { updatedUserInfo = updatedUserInfo.copy(name = it) },
                removeIndicator = true,
                placeholder = { kotex(text = "이름") },
                supportingText = { kotex(text = "이름은 언제든지 변경할 수 있습니다.", size = 13.sp) },
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .drawBehind {
                        val underLineOffsetStartX = size.width * 1 / 5
                        val underLineOffsetEndX = size.width * 4 / 5
                        val underLineOffsetY = size.height * 2 / 4 + 15F
                        drawLine(
                            color = MainColor,
                            start = Offset(x = underLineOffsetStartX, y = underLineOffsetY),
                            end = Offset(x = underLineOffsetEndX, y = underLineOffsetY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            )
        }
    }
}