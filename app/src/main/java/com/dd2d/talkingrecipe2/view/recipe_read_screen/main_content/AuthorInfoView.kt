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
import com.dd2d.talkingrecipe2.data_struct.AuthorInfo
import com.dd2d.talkingrecipe2.ui.CommonValue
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.SubColor
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun AuthorInfoView(
    modifier: Modifier = Modifier,
    authorInfo: AuthorInfo,
    recipeTitle: String,
    recipeDescription: String,
    onClickAuthor: ()->Unit,
    onClickFavorite: ()->Unit,
    onClickShare: ()->Unit,
    onClickSave: ()->Unit,
    onClickModify: ()->Unit,
){
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
            kotex(text = "${authorInfo.name} @${authorInfo.authorId}")
            kotex(text = recipeDescription)
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { onClickFavorite() }) {
                    Icon(imageVector = Icons.Default.Favorite, tint = HintText, contentDescription = "favorite this recipe")
                }
                IconButton(onClick = { onClickShare() }) {
                    Icon(imageVector = Icons.Default.Share, tint = HintText, contentDescription = "favorite this recipe")
                }
                IconButton(onClick = { onClickSave() }) {
                    Icon(imageVector = Icons.Default.Star, tint = HintText, contentDescription = "favorite this recipe")
                }
                IconButton(onClick = { onClickModify() }) {
                    Icon(imageVector = Icons.Default.Edit, tint = HintText, contentDescription = "favorite this recipe")
                }
            }
        }
        AsyncImage(
            model = authorInfo.profileImageUri,
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