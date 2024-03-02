package com.dd2d.talkingrecipe2.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreen
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreen
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode
import com.dd2d.talkingrecipe2.view_model.UserViewModel

fun NavGraphBuilder.recipeScreenGraph(
    navController: NavController,
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel
){
    composable(route = "${Screen.Recipe.route}/{recipeViewModelMode}"){ backStack->
        val modeArgument = backStack.arguments?.getString("recipeViewModelMode")?: "Error"

        var mode by remember { mutableStateOf(RecipeViewModelMode.nameOf(modeArgument)) }

        AnimatedContent(
            targetState = mode, label = "",
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
        ) { modeState->
            when(modeState){
                is RecipeViewModelMode.ReadMode -> {
                    RecipeReadScreen(
                        navController = navController,
                        userViewModel = userViewModel,
                        recipeViewModel = recipeViewModel,
                        onClickModify = { mode = RecipeViewModelMode.ModifyMode }
                    )
                }
                is RecipeViewModelMode.ModifyMode -> {
                    RecipeWriteScreen(
                        navController = navController,
                        userViewModel = userViewModel,
                        recipeViewModel = recipeViewModel,
                        recipeViewModelMode = modeState,
                        moveToRecipe = {
                            mode = RecipeViewModelMode.ReadMode
                            recipeViewModel.moveToMain()
                        }
                    )
                }
                is RecipeViewModelMode.WriteMode -> {
                    RecipeWriteScreen(
                        navController = navController,
                        userViewModel = userViewModel,
                        recipeViewModel = recipeViewModel,
                        recipeViewModelMode = modeState,
                        moveToRecipe = {
                            mode = RecipeViewModelMode.ReadMode
                            recipeViewModel.moveToMain()
                        }
                    )
                }
                is RecipeViewModelMode.OnModeError -> {
                    ErrorView(
                        cause = "error in select mode",
                        onClickBack = { navController.navigateUp() }
                    )
                }
            }

        }
    }
}