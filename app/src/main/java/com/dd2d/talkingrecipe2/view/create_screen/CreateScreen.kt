package com.dd2d.talkingrecipe2.view.create_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.view_model.CreateViewModel

@Composable
fun CreateScreen(
    modifier: Modifier = Modifier,
    createViewModel: CreateViewModel,
    onClickBack: () -> Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
    ){
        CreateScreenTopView(
            createScreenMode = createViewModel.createScreenMode,
            onClickBack = { onClickBack() }
        )
        CreateScreenCenterView(
            createViewModel = createViewModel
        )
    }
}

object CreateScreenValue{
    /** 50.dp*/
    val BottomButtonHeight = 50.dp
    /** 140.dp*/
    val StepInfoViewHeight = 140.dp
}