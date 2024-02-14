package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteStep
import com.dd2d.talkingrecipe2.view.recipe_write_screen.write_step.WriteStepMoveButton
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode

@Composable
fun RecipeWriteView(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    writeMode: RecipeViewModelMode,
    onChangeRecipe: (Recipe)->Unit,
    onClickBack: () -> Unit,
    onEndWrite: ()->Unit,
    requestErrorView: ()->Unit,
){
    var step by remember { mutableStateOf<RecipeWriteStep>(RecipeWriteStep.RecipeBasicInfo) }
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize()
    ){
        RecipeWriteScreenTopView(
            recipeMode = writeMode,
            onClickBack = { onClickBack() },
            requestErrorView = { requestErrorView() }
        )
        RecipeWriteScreenCenterView(
            recipe = recipe,
            onChangeRecipe = { res-> onChangeRecipe(res) },
            writeStep = step,
            modifier = modifier.weight(1F)
        )
        WriteStepMoveButton(
            writeStep = step,
            onClickNextStep = {
                if(step.ordinal == RecipeWriteStep.values().lastIndex) { onEndWrite() }
                else { step = nextStep(step) }
            },
            onClickPrevStep = { step = prevStep(step) },
        )
    }
}

private fun nextStep(current: RecipeWriteStep): RecipeWriteStep{
    if(current.ordinal == RecipeWriteStep.values().lastIndex) { return current }
    return RecipeWriteStep.values()[current.ordinal + 1]
}

private fun prevStep(current: RecipeWriteStep): RecipeWriteStep{
    if(current.ordinal == 0) { return current }
    return RecipeWriteStep.values()[current.ordinal - 1]
}