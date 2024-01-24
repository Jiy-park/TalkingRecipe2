package com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateStep
import com.dd2d.talkingrecipe2.navigation.CreateScreenMode
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreenCenterView
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreenTopView
import com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.write_step.WriteStepMoveButton

@Composable
fun CreateView(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onChangeRecipe: (Recipe)->Unit,
    writeStep: CreateStep,
    writeMode: CreateScreenMode,
    onClickBack: () -> Unit,
    onClickNextStep: ()->Unit,
    onClickPrevStep: ()->Unit,
){

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize()
    ){
        RecipeWriteScreenTopView(
            createScreenMode = writeMode,
            visible = writeStep.step != CreateStep.values().lastIndex,
            onClickBack = { onClickBack() },
        )
        RecipeWriteScreenCenterView(
            recipe = recipe,
            onChangeRecipe = { res-> onChangeRecipe(res) },
            createStep = writeStep,
            modifier = modifier.weight(1F)
        )
        WriteStepMoveButton(
            createStep = writeStep,
            visible = writeStep.step != CreateStep.values().lastIndex,
            onClickNextStep = { onClickNextStep() },
            onClickPrevStep = { onClickPrevStep() },
        )
    }
}