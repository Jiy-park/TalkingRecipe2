package com.dd2d.talkingrecipe2.view.recipe_read_screen.talking_recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.data_struct.AuthorInfo
import com.dd2d.talkingrecipe2.ui.CommonValue
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun SimpleAuthorInfoView(
    modifier: Modifier = Modifier,
    recipeTitle: String,
    authorInfo: AuthorInfo,
    onClickAuthor: ()->Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(CommonValue.SimpleAuthorInfoHeight)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxHeight()
                .weight(1F)
        ){
            kotex(text = recipeTitle, weight = FontWeight.Bold, size = 18.sp)
            kotex(text = "${authorInfo.name} @${authorInfo.authorId}", size = 13.sp)
        }

        AsyncImage(
            model = authorInfo.profileImageUri,
            contentDescription = "recipe author profile image",
            modifier = modifier
                .fillMaxHeight()
                .background(color = Color.LightGray, shape = CircleShape)
                .clip(CircleShape)
                .clickableWithoutRipple { onClickAuthor() }
                .aspectRatio(1F / 1F)
        )
    }
}

