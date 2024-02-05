package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteMode
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteStep
import com.dd2d.talkingrecipe2.view.recipe_write_screen.write_step.WriteStepMoveButton

@Composable
fun RecipeWriteView(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    writeStep: RecipeWriteStep,
    writeMode: RecipeWriteMode,
    onChangeRecipe: (Recipe)->Unit,
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
            onClickBack = { onClickBack() },
        )
        RecipeWriteScreenCenterView(
            recipe = recipe,
            onChangeRecipe = { res-> onChangeRecipe(res) },
            writeStep = writeStep,
            modifier = modifier.weight(1F)
        )
        WriteStepMoveButton(
            writeStep = writeStep,
            onClickNextStep = { onClickNextStep() },
            onClickPrevStep = { onClickPrevStep() },
        )
    }
}