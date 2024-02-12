package com.dd2d.talkingrecipe2.view.recipe_read_screen.main_content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun RecipeStepInfoView(
    modifier: Modifier = Modifier,
    stepInfoList: List<StepInfo>
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ){
        kotex(text = "레시피", weight = FontWeight.Bold, size = 18.sp)
        stepInfoList.forEach { stepInfo->
            StepInfoItemViewer(stepInfo = stepInfo)
            Divider(modifier = modifier.fillMaxWidth())
        }
    }
}

@Composable
fun StepInfoItemViewer(
    modifier: Modifier = Modifier,
    stepInfo: StepInfo
){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
    ) {
        kotex(
            text = "${stepInfo.order}.${stepInfo.description}",
            maxLine = Int.MAX_VALUE,
            modifier = modifier
                .weight(0.6F)
                .fillMaxHeight()
        )
        AsyncImage(
            model = stepInfo.imageUri,
            contentDescription = "step image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .weight(0.5F)
                .aspectRatio(1.6F / 1.2F)
                .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
        )
    }
}
