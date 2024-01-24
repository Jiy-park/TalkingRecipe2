package com.dd2d.talkingrecipe2.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.view.recipe_search_screen.RecipeSearchScreen

fun NavGraphBuilder.recipeSearchScreenGraph(
    navController: NavController
){
    composable(route = Screen.RecipeSearch.route){
        RecipeSearchScreen()
    }
}