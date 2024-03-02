package com.dd2d.talkingrecipe2.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.ui.TestingValue
import com.dd2d.talkingrecipe2.view.main_screen.MainScreen
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode
import com.dd2d.talkingrecipe2.view_model.UserViewModel

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel,
    requestLogout: ()->Unit,
){
    composable(route = Screen.Main.route){
        MainScreen(
            onClickSearch = { navController.navigate(route = Screen.Search.route) },
            onClickSavePost = { navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.SavePost.route}/${userViewModel.userId}") },
            onClickCreate = {
                val mode = RecipeViewModelMode.WriteMode
                navController.navigate(route = "${Screen.Recipe.route}/${mode.name}")
            },
            onClickMyPost = {
                userViewModel.userId
                navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.MyPost.route}/${userViewModel.userId}")
            },
            onClickSetting = {
                /*TODO("다이얼로그로 ")*/
//                    navController.navigate(route = "${Screen.RecipeRead.route}/$TestingRecipeId")
//                    onLogin = false
//                    userViewModel.logout()
                val mode = RecipeViewModelMode.ReadMode
                val recipeId = TestingValue.TestingRecipeId
                navController.navigate(route = "${Screen.Recipe.route}/${mode.name}")
                recipeViewModel.fetchRecipeById(recipeId)
            },
            onClickRecentRecipe = { recipeId->
                val mode = RecipeViewModelMode.ReadMode
                navController.navigate(route = "${Screen.Recipe.route}/$mode")
                recipeViewModel.fetchRecipeById(recipeId)
            },
        )
    }
}