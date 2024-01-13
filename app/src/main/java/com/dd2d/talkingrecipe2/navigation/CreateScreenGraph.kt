package com.dd2d.talkingrecipe2.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.view.create_screen.CreateScreen

fun NavGraphBuilder.createScreenGraph(
    navController: NavController
){
    composable(route = Screen.Create.route){
        CreateScreen()
    }
}