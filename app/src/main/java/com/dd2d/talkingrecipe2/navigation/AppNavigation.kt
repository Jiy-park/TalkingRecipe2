package com.dd2d.talkingrecipe2.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingRecipeId
import com.dd2d.talkingrecipe2.view.main_screen.MainScreen
import com.dd2d.talkingrecipe2.view.recipe_write_screen.CreateScreenValue

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
){
    val navController = rememberNavController()
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = Screen.Main.route,
    ){
        composable(route = Screen.Main.route){
            MainScreen(
                onClickSearchTrigger = { navController.navigate(route = Screen.RecipeSearch.route) },
                onClickSavePost = { navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.SavePost}") },
                onClickCreate = { navController.navigate(route = "${Screen.RecipeWrite.route}/${CreateScreenValue.CreateMode}") },
                onClickMyPost = { navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.MyPost}") },
                onClickSetting = {
                     /*TODO("다이얼로그로 ")*/
                    navController.navigate(route = "${Screen.RecipeRead.route}/$TestingRecipeId")
                },
                onClickRecentRecipe = { recipeId-> navController.navigate(route = "${Screen.RecipeRead.route}/$recipeId") },
            )
        }
        subScreenGraph(navController = navController)
        recipeWriteScreenGraph(navController = navController)
        recipeSearchScreenGraph(navController = navController)
        recipeReadScreenGraph(navController = navController)
    }
}