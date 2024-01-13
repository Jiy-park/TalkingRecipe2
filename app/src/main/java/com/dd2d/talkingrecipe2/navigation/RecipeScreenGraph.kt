package com.dd2d.talkingrecipe2.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.view.recipe_screen.RecipeScreen

fun NavGraphBuilder.recipeScreenGraph(
    navController: NavController
){
    composable(route = "${Screen.Recipe.route}/{recipeId}"){ backStack->
        backStack.arguments?.getString("recipeId")?.let { recipeId->
            RecipeScreen(
                recipeId = recipeId.toInt()
            )
        }
    }
}