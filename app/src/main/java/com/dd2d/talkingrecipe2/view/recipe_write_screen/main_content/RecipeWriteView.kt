package com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe_create.WriteStep
import com.dd2d.talkingrecipe2.navigation.CreateScreenMode
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreenCenterView
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreenTopView
import com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.write_step.WriteStepMoveButton

@Composable
fun RecipeWriteView(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    writeStep: WriteStep,
    writeMode: CreateScreenMode,
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