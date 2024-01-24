package com.dd2d.talkingrecipe2.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.data_struct.AuthorInfo
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.model.RecipeRepository
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreen
import com.dd2d.talkingrecipe2.view.recipe_read_screen.talking_recipe.TalkingRecipe
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.recipeScreenGraph(
    navController: NavController,
){
    composable(route = "${Screen.RecipeRead.route}/{recipeId}"){ backStack->
        val recipeId = backStack.arguments?.getString("recipeId")
        val recipeRepository = RecipeRepository()
        val recipeViewModel = RecipeViewModel(recipeRepo = recipeRepository, recipeId = recipeId)

        var recipeReadMode by remember { mutableStateOf<RecipeReadMode>(RecipeReadMode.Normal) }

        AnimatedContent(
            targetState = recipeReadMode, label = "",
            transitionSpec = {
                slideInVertically { it*2 } togetherWith
                        slideOutVertically { it*2 }
            }
        ) { readMode->
            when(readMode){
                is RecipeReadMode.Normal -> {
                    RecipeReadScreen(
                        recipeViewModel = recipeViewModel,
                        onClickBack = { navController.navigateUp() },
                        onClickModify = { navController.navigate(route = "${Screen.RecipeWrite.route}/${recipeId}") },
                        onClickTalkingRecipe = { authorInfo, recipe->
                            recipeReadMode = RecipeReadMode.TalkingRecipe(authorInfo = authorInfo, recipe = recipe)
                        }
                    )
                }
                is RecipeReadMode.TalkingRecipe -> {
                    val authorInfo = readMode.authorInfo
                    val recipe = readMode.recipe
                    TalkingRecipe(
                        authorInfo = authorInfo,
                        recipe = recipe,
                        onClickBack = {
                            recipeReadMode = RecipeReadMode.Normal
                        },
                        onClickAuthor = {  },
                        onClickToMain = {
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}

sealed class RecipeReadMode{
    object Normal: RecipeReadMode()
    class TalkingRecipe(val recipe: Recipe, val authorInfo: AuthorInfo): RecipeReadMode()
}
