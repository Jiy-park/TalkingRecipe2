package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteState
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteStep
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.recipe_write_screen.write_step.EndWrite
import com.dd2d.talkingrecipe2.view_model.RecipeWriteViewModel

@Composable
fun RecipeWriteScreen(
    writeViewModel: RecipeWriteViewModel,
    onClickBack: () -> Unit,
    onClickMoveToMain: ()->Unit,
    onClickMoveToRecipe: (recipeId: String)->Unit,
){
    val writeState by writeViewModel.writeState.collectAsState()
    val recipe by writeViewModel.recipe.collectAsState()
    val writeStep by writeViewModel.writeStep.collectAsState()

    when(writeState){
        is RecipeWriteState.Init -> {
            writeViewModel.init()
        }
        is RecipeWriteState.OnFetching -> {
            LoadingView()
        }
        is RecipeWriteState.Stable -> {
            RecipeWriteView(
                recipe = recipe,
                onChangeRecipe = { update-> writeViewModel.onChangeRecipe(update) },
                writeStep = writeStep,
                writeMode = writeViewModel.writeScreenMode,
                onClickBack = { onClickBack() },
                onClickNextStep = {
                    if(writeStep == RecipeWriteStep.RecipeThumbnail){
                        writeViewModel.endWrite()
                    }
                    else{
                        writeViewModel.moveToNextStep()
                    }
                },
                onClickPrevStep = { writeViewModel.moveToPrevStep() },
            )
        }
        is RecipeWriteState.OnUploading -> {
            LoadingView()
        }
        is RecipeWriteState.OnEndUploading -> {
            EndWrite(
                onClickMoveToMain = { onClickMoveToMain() },
                onClickMoveToRecipe = { onClickMoveToRecipe(recipe.basicInfo.recipeId) }
            )
        }
        is RecipeWriteState.OnError -> {
            ErrorView(cause = (writeState as RecipeWriteState.OnError).cause){ onClickBack() }
        }
    }
}

object CreateScreenValue{
    /** "createMode"*/
    const val CreateMode = "createMode"
}