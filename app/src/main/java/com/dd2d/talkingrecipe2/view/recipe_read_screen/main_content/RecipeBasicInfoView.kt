package com.dd2d.talkingrecipe2.view.recipe_read_screen.main_content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.kotex


@Composable
fun RecipeBasicInfoView(
    modifier: Modifier = Modifier,
    basicInfo: RecipeBasicInfo
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        RecipeBasicInfoItemViewer(
            value = basicInfo.time,
            iconResId = R.drawable.ic_time,
            modifier = modifier.weight(1F)
        )
        RecipeBasicInfoItemViewer(
            value = basicInfo.amount,
            iconResId = R.drawable.ic_person,
            modifier = modifier.weight(1F)
        )
        RecipeBasicInfoItemViewer(
            value = basicInfo.calorie,
            iconResId = R.drawable.ic_calorie,
            modifier = modifier.weight(1F)
        )
        RecipeBasicInfoItemViewer(
            value = basicInfo.level.description,
            iconResId = R.drawable.ic_level,
            modifier = modifier.weight(1F)
        )
    }
}

@Composable
fun RecipeBasicInfoItemViewer(
    modifier: Modifier = Modifier,
    value: String,
    iconResId: Int,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .height(50.dp)
    ){
        Icon(painter = painterResource(id = iconResId), tint = HintText, contentDescription = null)
        kotex(text = value)
    }
}
