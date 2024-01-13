package com.dd2d.talkingrecipe2.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.view.search_screen.SearchScreen

fun NavGraphBuilder.searchScreenGraph(
    navController: NavController
){
    composable(route = Screen.Search.route){
        SearchScreen()
    }
}