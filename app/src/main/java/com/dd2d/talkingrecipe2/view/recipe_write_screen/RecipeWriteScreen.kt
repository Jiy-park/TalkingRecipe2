package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.CreateView
import com.dd2d.talkingrecipe2.view_model.RecipeWriteViewModel
import com.dd2d.talkingrecipe2.view_model.WriteState

@Composable
fun RecipeWriteScreen(
    writeViewModel: RecipeWriteViewModel,
    onClickBack: () -> Unit,
    onClickMoveToMain: ()->Unit,
    onClickMoveToRecipe: ()->Unit,
){
    val createState by writeViewModel.writeState.collectAsState()
    val recipe by writeViewModel.recipe.collectAsState()
    val writeStep by writeViewModel.writeStep.collectAsState()

    when(createState){
        is WriteState.Init -> {
            writeViewModel.init()
        }
        is WriteState.OnFetching -> {
            LoadingView()
        }
        is WriteState.Stable -> {
            CreateView(
                recipe = recipe,
                onChangeRecipe = { update-> writeViewModel.onChangeRecipe(update) },
                writeStep = writeStep,
                writeMode = writeViewModel.writeScreenMode,
                onClickBack = { onClickBack() },
                onClickNextStep = { writeViewModel.moveToNextStep() },
                onClickPrevStep = { writeViewModel.moveToPrevStep() },
            )
        }
        is WriteState.OnUploading -> {
            LoadingView()
        }
        is WriteState.OnEnd -> {
//            TODO("레시피 업로드 완료 후 나오는 페이지.")
        }
        is WriteState.OnError -> {
            ErrorView(cause = (createState as WriteState.OnError).cause){ onClickBack() }
        }
    }
}

object CreateScreenValue{
    /** "createMode"*/
    const val CreateMode = "createMode"
}