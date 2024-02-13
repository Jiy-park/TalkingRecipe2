package com.dd2d.talkingrecipe2.view.recipe_read_screen.main_content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.data_struct.LocalUser
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.ui.CommonValue
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.SubColor
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun AuthorInfoView(
    modifier: Modifier = Modifier,
    authorInfo: SimpleUserInfo,
    recipeTitle: String,
    recipeDescription: String,
    isSavePost: Boolean,
    isFavoritePost: Boolean,
    onClickAuthor: ()->Unit,
    onClickFavorite: (Boolean)->Unit,
    onClickShare: ()->Unit,
    onClickSave: (Boolean)->Unit,
    onClickModify: ()->Unit,
){
    val user = LocalUser.current
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(CommonValue.AuthorInfoHeight)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = modifier
                .weight(1F)
                .fillMaxHeight()
        ){
            kotex(text = recipeTitle, weight = FontWeight.Bold, size = 20.sp)
            kotex(text = authorInfo.fullName)
            kotex(text = recipeDescription)
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { onClickFavorite(!isFavoritePost) }) {
                    Icon(imageVector = Icons.Default.Favorite, tint = if(isFavoritePost) SubColor else HintText, contentDescription = "favorite this recipe")
                }
                IconButton(onClick = { onClickShare() }) {
                    Icon(imageVector = Icons.Default.Share, tint = HintText, contentDescription = "share this recipe")
                }
                IconButton(onClick = { onClickSave(!isSavePost) }) {
                    Icon(imageVector = Icons.Default.Star, tint = if(isSavePost) SubColor else HintText, contentDescription = "save this recipe")
                }
                if(authorInfo.userId == user.userId){
                    IconButton(onClick = { onClickModify() }) {
                        Icon(imageVector = Icons.Default.Edit, tint = HintText, contentDescription = "modify this recipe")
                    }
                }
            }
        }
        AsyncImage(
            model = authorInfo.userProfileImageUri,
            contentDescription = "recipe author profile image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxHeight()
                .aspectRatio(1F / 1F)
                .clickableWithoutRipple { onClickAuthor() }
                .background(color = Color.White, shape = CircleShape)
                .clip(CircleShape)
                .border(width = 2.dp, color = SubColor, shape = CircleShape)
        )
    }
}