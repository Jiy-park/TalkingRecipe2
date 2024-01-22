package com.dd2d.talkingrecipe2.view.main_screen

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.ui.CircleIconButton
import com.dd2d.talkingrecipe2.ui.RecipeViewer
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.ui.theme.SubColor
import com.dd2d.talkingrecipe2.ui.theme.kopupFontFamily

@Composable
fun MainScreenBottomView(
    modifier: Modifier = Modifier,
    onClickSavePost: ()->Unit,
    onClickCreate: ()->Unit,
    onClickMyPost: ()->Unit,
    onClickSetting: ()->Unit,
){
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        modifier = modifier
            .zIndex(1F)
            .fillMaxWidth()
            .height(310.dp)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .align(Alignment.TopCenter)
                    .height(70.dp)
            ) {
                CircleIconButton(
                    iconRes = R.drawable.ic_locker,
                    text = "보관함",
                    color = MainColor,
                    onClick = { onClickSavePost() },
                    modifier = modifier.weight(1F)
                )
                CircleIconButton(
                    iconRes = R.drawable.ic_create,
                    text = "만들기",
                    color = SubColor,
                    onClick = { onClickCreate() },
                    modifier = modifier.weight(1F)
                )
                CircleIconButton(
                    iconRes = R.drawable.outline_toxi_head,
                    text = "내정보",
                    color = MainColor,
                    onClick = { onClickMyPost() },
                    modifier = modifier.weight(1F)
                )
                CircleIconButton(
                    iconRes = R.drawable.ic_setting,
                    text = "설정",
                    color = SubColor,
                    onClick = { onClickSetting() },
                    modifier = modifier.weight(1F)
                )
            }
        }
    }
}

@Composable
fun RecentRecipe(
    modifier: Modifier = Modifier,
    recipeBasicInfo: RecipeBasicInfo,
    recipeAuthor: String,
    recipeThumbnail: Uri,
    onClick: (recipeId: String)->Unit,
){
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Text(text = "최근 본 레시피", fontFamily = kopupFontFamily, fontWeight = FontWeight.Medium, color = MainText, fontSize = 15.sp, modifier = modifier.fillMaxWidth())
        RecipeViewer(
            recipeBasicInfo = recipeBasicInfo,
            recipeAuthor = recipeAuthor,
            recipeThumbnail = recipeThumbnail,
            onClick = { recipeId-> onClick(recipeId) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}