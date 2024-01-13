package com.dd2d.talkingrecipe2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGray
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.ui.theme.kopupFontFamily
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun RecipeViewer(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onClick: (recipeId: Int) -> Unit,
){
    val innerModifier = Modifier
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .clickableWithoutRipple { onClick(recipe.recipeId) }
    ) {
        Surface(shape = RoundedCornerShape(15.dp)) {
            Image(painter = painterResource(id = recipe.thumbnailPath), contentDescription = "thumbnail image")
        }
        Spacer(modifier = innerModifier.width(15.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = innerModifier
                .fillMaxWidth()
        ){
            kotex(text = recipe.title, weight = FontWeight.Bold, size = 20.sp)

            Spacer(modifier = modifier.height(5.dp))

            kotex(text = recipe.author)
            kotex(text = recipe.description)

            Spacer(modifier = modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.spacedBy(5.dp),
                modifier = modifier
                    .horizontalScroll(state = rememberScrollState())
            ) {
                TagView(text = "${recipe.time}분")
                TagView(text = "${recipe.amount}인분")
                TagView(text = recipe.level.description)
                TagView(text = "${recipe.calorie}kcal")
            }
        }
    }
}

@Composable
fun TagView(
    modifier: Modifier = Modifier,
    text: String
){
    Surface(
        color = BackgroundGray,
        shape = RoundedCornerShape(5.dp),
        modifier = modifier
            .wrapContentSize()
    ) {
        kotex(
            text = text,
            color = Color.White,
            size = 12.sp,
            modifier = modifier
                .padding(horizontal = 5.dp)
        )
    }
}

@Composable
fun CircleIconButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    text: String,
    color: Color,
    onClick: ()->Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickableWithoutRipple { onClick() }
    ){
        Surface(
            shape = CircleShape,
            color = color,
            modifier = modifier
                .aspectRatio(1F/1F)
        ) {
            Icon(painter = painterResource(id = iconRes), tint = Color.White, contentDescription = "icon", modifier = modifier.padding(10.dp))
        }
        Text(text = text, fontFamily = kopupFontFamily, fontWeight = FontWeight.Light, fontSize = 15.sp, color = MainText)
    }
}