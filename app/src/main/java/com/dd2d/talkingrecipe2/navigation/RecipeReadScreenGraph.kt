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
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.navigation.RecipeReadMode.Normal
import com.dd2d.talkingrecipe2.navigation.RecipeReadMode.TalkingRecipe
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreen
import com.dd2d.talkingrecipe2.view.recipe_read_screen.talking_recipe.TalkingRecipe
import com.dd2d.talkingrecipe2.view_model.RecipeReadViewModel

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.recipeReadScreenGraph(
    navController: NavController,
    onClickSavePost: (recipeId: String)->Unit,
    updateUserRecentRecipe: (recipeId: String)->Unit,
){
    composable(route = "${Screen.RecipeRead.route}/{recipeId}"){ backStack->
        val recipeId = backStack.arguments?.getString("recipeId")
        val recipeRepository = RecipeFetchRepositoryImpl()
        val recipeViewModel = RecipeReadViewModel(recipeRepo = recipeRepository, recipeId = recipeId)

        var recipeReadMode by remember { mutableStateOf<RecipeReadMode>(Normal) }

        AnimatedContent(
            targetState = recipeReadMode, label = "",
            transitionSpec = {
                slideInVertically { it*2 } togetherWith
                        slideOutVertically { it*2 }
            }
        ) { readMode->
            when(readMode){
                is Normal -> {
                    RecipeReadScreen(
                        recipeViewModel = recipeViewModel,
                        onClickBack = { navController.navigateUp() },
                        onClickModify = { navController.navigate(route = "${Screen.RecipeWrite.route}/${recipeId}") },
                        onClickTalkingRecipe = { authorInfo, recipe->
                            recipeReadMode = TalkingRecipe(authorInfo = authorInfo, recipe = recipe)
                        },
                        onClickAuthorProfileImage = {

                        },
                        isSavePost = true,
                        onClickSave = { recipeId->
                            onClickSavePost(recipeId)
                        },
                        onClickShare = {

                        },
                        onClickFavorite = {

                        }
                    )
                }
                is TalkingRecipe -> {
                    val authorInfo = readMode.authorInfo
                    val recipe = readMode.recipe
                    TalkingRecipe(
                        authorInfo = authorInfo,
                        recipe = recipe,
                        onClickBack = {
                            recipeReadMode = Normal
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

/** 레시피를 읽을 때의 모드.
 *- [Normal]
 *- [TalkingRecipe]*/
sealed class RecipeReadMode{
    /** 레시피의 대부분의 정보를 볼 수 있음.*/
    object Normal: RecipeReadMode()
    /** 레시피를 음성으로 조작할 수 있음.*/
    class TalkingRecipe(val recipe: Recipe, val authorInfo: AuthorInfo): RecipeReadMode()
}
