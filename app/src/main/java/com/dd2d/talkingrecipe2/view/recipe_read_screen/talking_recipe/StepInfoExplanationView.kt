package com.dd2d.talkingrecipe2.view.recipe_read_screen.talking_recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun StepInfoExplanationView(
    modifier: Modifier = Modifier,
    explanation: String,
    onClickToxi: ()->Unit,
) {
    val innerModifier = Modifier
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxWidth()
    ){
        kotex(
            text = explanation,
            maxLine = Int.MAX_VALUE,
            modifier = innerModifier
                .fillMaxWidth()
                .weight(1F)
        )
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = innerModifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickableWithoutRipple { onClickToxi() }
        ) {
            AsyncImage(
                model = R.drawable.main_screen_toxi,
                contentDescription = "",
                innerModifier.size(100.dp)
            )
        }
    }
}
