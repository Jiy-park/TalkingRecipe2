package com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateStep
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreenCenterView
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreenTopView
import com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.write_step.WriteStepMoveButton
import com.dd2d.talkingrecipe2.view_model.CreateViewModel

@Composable
fun CreateView(
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
        RecipeWriteScreenTopView(
            createScreenMode = createViewModel.createScreenMode,
            visible = createStep.step != CreateStep.values().lastIndex,
            onClickBack = { onClickBack() },
        )
        RecipeWriteScreenCenterView(
            createViewModel = createViewModel,
            createStep = createStep,
            onClickMoveToMain = { onClickMoveToMain() },
            onClickMoveToRecipe = { onClickMoveToRecipe() },
            modifier = modifier.weight(1F)
        )
        WriteStepMoveButton(
            createStep = createStep,
            visible = createStep.step != CreateStep.values().lastIndex,
            onClickNextStep = { createViewModel.moveToNextStep() },
            onClickPrevStep = { createViewModel.moveToPrevStep() },
        )
    }
}