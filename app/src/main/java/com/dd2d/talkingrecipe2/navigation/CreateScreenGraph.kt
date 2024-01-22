package com.dd2d.talkingrecipe2.navigation

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.view.create_screen.CreateScreen
import com.dd2d.talkingrecipe2.view_model.CreateViewModel

const val TestingRecipeId = "TalkingRecipe_240115_0107"
const val TestingUserId = "TalkingRecipe"

fun NavGraphBuilder.createScreenGraph(
    navController: NavController
){

    composable(route = "${Screen.Create.route}/{recipeId}"){ backStack->
        /** 레시피 수정 모드. 수정 모드일 경우 [CreateScreen]에 매개변수로 수정할 레시피의 아이디값을 넘겨줌. 아닐 경우 [CreateScreen.CreateMode]을 받음.*/
        val mode = backStack.arguments?.getString("recipeId")?.let { param->
            if(param == CreateScreen.CreateMode) CreateScreenMode.Create
            else CreateScreenMode.Modify(param)
        }?: CreateScreenMode.Create

        val application = LocalContext.current.applicationContext as Application
        val createViewModel: CreateViewModel = viewModel {
            CreateViewModel(userId = TestingUserId, createScreenMode = mode, application = application)
        }
        CreateScreen(
            createViewModel = createViewModel,
            onClickBack = { navController.navigateUp() },
            onClickMoveToMain = { navController.navigateUp() },
            onClickMoveToRecipe = {
                val recipeId = createViewModel.recipeBasicInfo.recipeId
                navController.navigate(route = "${Screen.Recipe.route}/$recipeId")
            }
        )
    }
}

sealed class CreateScreenMode{
    object Create: CreateScreenMode()
    class Modify(val recipeId: String): CreateScreenMode()
}

object CreateScreen{
    const val CreateMode = "createMode"
}