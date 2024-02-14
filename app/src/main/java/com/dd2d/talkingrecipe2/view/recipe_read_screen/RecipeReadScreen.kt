package com.dd2d.talkingrecipe2.view.recipe_read_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadMode.Normal
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadMode.TalkingRecipe
import com.dd2d.talkingrecipe2.view.recipe_read_screen.main_content.RecipeReadView
import com.dd2d.talkingrecipe2.view.recipe_read_screen.talking_recipe.TalkingRecipe
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState
import com.dd2d.talkingrecipe2.view_model.UserViewModel


@Composable
fun RecipeReadScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel,
    onClickModify: () -> Unit
){
    var isSavePost by remember { mutableStateOf(false) }
    var isFavoritePost by remember { mutableStateOf(false) }

    val recipe by recipeViewModel.recipe.collectAsState()
    val state by recipeViewModel.state.collectAsState()

    when(state){
        is RecipeViewModelState.OnConnected -> { LoadingView() }
        is RecipeViewModelState.OnStable -> {
            StableView(
                recipe = recipe,
                isFavoritePost = isFavoritePost,
                isSavePost = isSavePost,
                onClickFavorite = { update-> isFavoritePost = update},
                onClickSave = { update-> isSavePost = update},
                onClickBack = { navController.navigateUp() },
                onClickAuthor = { author->  },
                onClickShare = { /*TODO*/ },
                onClickModify = { onClickModify() },
                onClickToMain = { navController.navigateUp() }
            )
        }
        is RecipeViewModelState.OnError -> {
            ErrorView(
                cause = (state as RecipeViewModelState.OnError).msg,
                onClickBack = { navController.navigateUp() }
            )
        }
    }


}
@Composable
private fun StableView(
    recipe: Recipe,
    isSavePost: Boolean,
    isFavoritePost: Boolean,
    onClickFavorite: (Boolean)->Unit,
    onClickSave: (Boolean)->Unit,
    onClickBack: () -> Unit,
    onClickAuthor: (author: SimpleUserInfo)->Unit,
    onClickShare: ()->Unit,
    onClickModify: () -> Unit,
    onClickToMain: ()->Unit,
){
    var recipeReadMode by remember { mutableStateOf<RecipeReadMode>(Normal) }

    AnimatedContent(
        targetState = recipeReadMode, label = "",
        transitionSpec = {
            slideInVertically { it*2 } togetherWith slideOutVertically { it*2 }
        }
    ) {readMode->
        when(readMode){
            is Normal -> {
                RecipeReadView(
                    recipe = recipe,
                    isSavePost = isSavePost,
                    isFavoritePost = isFavoritePost,
                    onClickBack = { onClickBack() },
                    onClickAuthorProfileImage = { author ->  onClickAuthor(author) },
                    onClickFavorite = { update-> onClickFavorite(update) },
                    onClickSave = { update-> onClickSave(update) },
                    onClickShare = { onClickShare() },
                    onClickModify = { onClickModify() },
                    onClickTalkingRecipe = { recipeReadMode = TalkingRecipe }
                )
            }
            is TalkingRecipe -> {
                TalkingRecipe(
                    recipe = recipe,
                    onClickBack = { recipeReadMode = Normal },
                    onClickAuthor = { author-> onClickAuthor(author) },
                    onClickToMain = { onClickToMain() }
                )
            }
        }
    }
}

/** 레시피를 읽을 때의 모드.
 *- [Normal]
 *- [TalkingRecipe]*/
private sealed class RecipeReadMode{
    /** 레시피의 대부분의 정보를 볼 수 있음.*/
    object Normal: RecipeReadMode()
    /** 레시피를 음성으로 조작할 수 있음.*/
    object TalkingRecipe: RecipeReadMode()
}