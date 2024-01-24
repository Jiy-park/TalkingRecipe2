package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateState
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.CreateView
import com.dd2d.talkingrecipe2.view_model.CreateViewModel

@Composable
fun RecipeWriteScreen(
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
            CreateView(
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
            ErrorView(cause = (createState as CreateState.OnError).msg){ onClickBack() }
        }
    }
}

object CreateScreenValue{
    /** "createMode"*/
    const val CreateMode = "createMode"
}