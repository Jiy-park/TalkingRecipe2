package com.dd2d.talkingrecipe2.view.create_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.create_screen.CreateScreenValue.BottomButtonHeight
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeBasicInfo
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeStepInfo
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateStepMoveButton
import com.dd2d.talkingrecipe2.view_model.CreateState
import com.dd2d.talkingrecipe2.view_model.CreateStep
import com.dd2d.talkingrecipe2.view_model.CreateViewModel


@Composable
fun CreateScreenCenterView(
    modifier: Modifier = Modifier,
    createViewModel: CreateViewModel
){
    val createState by createViewModel.createState.collectAsState()
    val createStep by createViewModel.createStep.collectAsState()
    if(createState is CreateState.OnFetching){
        LoadingView()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .padding(bottom = BottomButtonHeight)
        ){
            when(createStep){
                CreateStep.RecipeBasicInfo -> {
                    CreateRecipeBasicInfo(createViewModel = createViewModel)
                }
                CreateStep.RecipeStepInfo -> {
                    CreateRecipeStepInfo(createViewModel = createViewModel)
                }
                CreateStep.RecipeThumbnail -> {
                    CreateRecipeThumbnail()
                }
                CreateStep.EndCreate -> {
                    CreateRecipeEnd()
                }
            }
        }

        CreateStepMoveButton(
            createStep = createStep,
            onClickNextStep = { createViewModel.moveToNextStep() },
            onClickPrevStep = { createViewModel.moveToPrevStep() },
            modifier = modifier.align(Alignment.BottomCenter).height(BottomButtonHeight)
        )
    }
}
@Composable
fun CreateRecipeIngredient(
    modifier: Modifier = Modifier,
){
    Box(modifier = modifier.fillMaxSize()){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Cyan)
        ){
            kotex(text = "ingredient")
        }
    }
}

@Composable
fun CreateRecipeThumbnail(
    modifier: Modifier = Modifier,
){
    Box(modifier = modifier.fillMaxSize()){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Blue)
        ){
            kotex(text = "thumbnail")
        }
    }
}

@Composable
fun CreateRecipeEnd(
    modifier: Modifier = Modifier,
){
    Box(modifier = modifier.fillMaxSize()){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Green)
        ){
            kotex(text = "end")
        }
    }
}