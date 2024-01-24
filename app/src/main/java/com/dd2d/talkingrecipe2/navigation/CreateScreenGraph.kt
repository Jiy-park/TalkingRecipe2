package com.dd2d.talkingrecipe2.navigation

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingUserId
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreen
import com.dd2d.talkingrecipe2.view.recipe_write_screen.CreateScreenValue
import com.dd2d.talkingrecipe2.view_model.CreateViewModel

fun NavGraphBuilder.createScreenGraph(
    navController: NavController
){

    composable(route = "${Screen.RecipeWrite.route}/{recipeId}"){ backStack->
        /** 레시피 수정 모드. 수정 모드일 경우 [RecipeWriteScreen]에 매개변수로 수정할 레시피의 아이디값을 넘겨줌. 아닐 경우 [CreateScreenValue.CreateMode]을 받음.*/
        val mode = backStack.arguments?.getString("recipeId")?.let { recipeId->
            if(recipeId == CreateScreenValue.CreateMode) CreateScreenMode.Create
            else CreateScreenMode.Modify(recipeId)
        }?: CreateScreenMode.Create

//        val mode = CreateScreenMode.Modify("TalkingRecipe_240124_1526")

        val application = LocalContext.current.applicationContext as Application
        val createViewModel: CreateViewModel = viewModel {
            CreateViewModel(userId = TestingUserId, createScreenMode = mode, application = application)
        }
        RecipeWriteScreen(
            createViewModel = createViewModel,
            onClickBack = { navController.navigateUp() },
            onClickMoveToMain = { navController.navigateUp() },
            onClickMoveToRecipe = {
                val recipeId = createViewModel.recipeBasicInfo.recipeId
                navController.navigate(route = "${Screen.RecipeRead.route}/$recipeId")
            }
        )
    }
}

sealed class CreateScreenMode{
    object Create: CreateScreenMode()
    class Modify(val recipeId: String): CreateScreenMode()
}