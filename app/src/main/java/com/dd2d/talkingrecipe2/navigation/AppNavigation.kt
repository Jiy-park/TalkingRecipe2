package com.dd2d.talkingrecipe2.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dd2d.talkingrecipe2.alog
import com.dd2d.talkingrecipe2.view.create_screen.CreateScreen
import com.dd2d.talkingrecipe2.view.main_screen.MainScreen

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
                onClickSearchTrigger = { navController.navigate(route = Screen.Search.route) },
                onClickSavePost = { navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.SavePost}") },
                onClickCreate = { navController.navigate(route = "${Screen.Create.route}/${CreateScreen.CreateMode}") },
                onClickMyPost = { navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.MyPost}") },
                onClickSetting = { /*TODO("다이얼로그로 ")*/ },
                onClickRecentRecipe = { recipeId-> navController.navigate(route = "${Screen.Recipe.route}/$recipeId") },
            )
        }
        subScreenGraph(navController = navController)
        createScreenGraph(navController = navController)
        searchScreenGraph(navController = navController)
        recipeScreenGraph(navController = navController)
    }
}