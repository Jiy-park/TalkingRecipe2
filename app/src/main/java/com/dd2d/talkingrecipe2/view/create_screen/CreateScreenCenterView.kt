package com.dd2d.talkingrecipe2.view.create_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dd2d.talkingrecipe2.logging
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeBasicInfo
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateStepMoveButton
import com.dd2d.talkingrecipe2.view_model.CreateState
import com.dd2d.talkingrecipe2.view_model.CreateStep
import com.dd2d.talkingrecipe2.view_model.CreateViewModel
import com.dd2d.talkingrecipe2.view_model.Ingredient


@Composable
fun CreateScreenCenterView(
    modifier: Modifier = Modifier,
    createViewModel: CreateViewModel
){
    logging("sd")
    logging("sdd")
    val createState by createViewModel.createState.collectAsState()
    val createStep by createViewModel.createStep.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ){
        when(createStep){
            CreateStep.RecipeBasicInfo -> {
                CreateRecipeBasicInfo(
                    recipeBasicInfo = createViewModel.recipeBasicInfo,
                    ingredientList = createViewModel.ingredientList,
                    onChangeIngredient = { index, ingredient -> createViewModel.ingredientList[index] = ingredient },
                    onCLickAdd = { createViewModel.ingredientList.add(Ingredient()) },
                    onClickRemove = { index-> createViewModel.ingredientList.removeAt(index) },
                    onChangeRecipeBasicInfo = { createViewModel.recipeBasicInfo = it },
                )
            }
            CreateStep.RecipeStepInfo -> {
                CreateRecipeStepInfo()
            }
            CreateStep.RecipeThumbnail -> {
                CreateRecipeThumbnail()
            }
            CreateStep.EndCreate -> {
                CreateRecipeEnd()
            }
        }

        CreateStepMoveButton(
            modifier = modifier.align(Alignment.BottomCenter),
            createStep = createStep,
            onNextStep = { createViewModel.moveToNextStep() },
            onPrevStep = { createViewModel.moveToPrevStep() }
        )

        if(createState is CreateState.StartFetch){
            LoadingView()
        }
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
fun CreateRecipeStepInfo(
    modifier: Modifier = Modifier,
){
    Box(modifier = modifier.fillMaxSize()){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Gray)
        ){
            kotex(text = "step info")
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