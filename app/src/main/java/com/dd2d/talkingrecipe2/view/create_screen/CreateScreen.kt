package com.dd2d.talkingrecipe2.view.create_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateState
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateStep
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateStepMoveButton
import com.dd2d.talkingrecipe2.view_model.CreateViewModel

@Composable
fun CreateScreen(
    createViewModel: CreateViewModel,
    onClickBack: () -> Unit,
    onClickMoveToMain: ()->Unit,
    onClickMoveToRecipe: ()->Unit,
){
    val createState by createViewModel.createState.collectAsState()

    when(createState){
        is CreateState.Init -> {
            createViewModel.init()
        }
        is CreateState.OnFetching -> {
            LoadingView()
        }
        is CreateState.Stable -> {
            StableView(
                createViewModel = createViewModel,
                onClickBack = { onClickBack() },
                onClickMoveToMain = { onClickMoveToMain() },
                onClickMoveToRecipe = { onClickMoveToRecipe() },
            )
        }
        is CreateState.OnUploading -> {
            LoadingView()
        }
        is CreateState.OnError -> {
            ErrorView(cause = (createState as CreateState.OnError).msg)
        }
    }
}


@Composable
fun StableView(
    modifier: Modifier = Modifier,
    createViewModel: CreateViewModel,
    onClickBack: () -> Unit,
    onClickMoveToMain: () -> Unit,
    onClickMoveToRecipe: () -> Unit,
){
    val createStep by createViewModel.createStep.collectAsState()

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize()
    ){
        CreateScreenTopView(
            createScreenMode = createViewModel.createScreenMode,
            visible = createStep.step != CreateStep.values().lastIndex,
            onClickBack = { onClickBack() },
        )
        CreateScreenCenterView(
            createViewModel = createViewModel,
            createStep = createStep,
            onClickMoveToMain = { onClickMoveToMain() },
            onClickMoveToRecipe = { onClickMoveToRecipe() },
            modifier = modifier.weight(1F)
        )
        CreateStepMoveButton(
            createStep = createStep,
            visible = createStep.step != CreateStep.values().lastIndex,
            onClickNextStep = { createViewModel.moveToNextStep() },
            onClickPrevStep = { createViewModel.moveToPrevStep() },
        )
    }
}


object CreateScreenValue{
    /** 50.dp*/
    val BottomButtonHeight = 50.dp
    /** 60.dp*/
    val TopViewHeight = 60.dp
    /** 140.dp*/
    val StepInfoViewHeight = 140.dp
}