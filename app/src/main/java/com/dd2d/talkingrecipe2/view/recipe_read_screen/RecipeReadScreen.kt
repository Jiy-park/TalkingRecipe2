package com.dd2d.talkingrecipe2.view.recipe_read_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.logging
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingAuthor
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.recipe_read_screen.main_content.RecipeReadView
import com.dd2d.talkingrecipe2.view_model.RecipeReadViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeState

@Composable
fun RecipeReadScreen(
    modifier: Modifier = Modifier,
    recipeViewModel: RecipeReadViewModel,
    onClickBack: () -> Unit,
    onClickAuthorProfileImage: () -> Unit,
    onClickFavorite: () -> Unit,
    onClickShare: () -> Unit,
    isSavePost: Boolean,
    onClickSave: (recipeId: String) -> Unit,
    onClickModify: () -> Unit,
    onClickTalkingRecipe: (SimpleUserInfo, Recipe) -> Unit = { _, _ -> logging("click talking recipe") },
){
    val recipeState by recipeViewModel.recipeState.collectAsState()

    when(recipeState){
        is RecipeState.Init -> { recipeViewModel.init() }
        is RecipeState.OnLoading -> { LoadingView() }
        is RecipeState.Stable -> {
            val recipe by recipeViewModel.recipe.collectAsState()
            val authorInfo = TestingAuthor
            RecipeReadView(
                recipe = recipe,
                authorInfo = authorInfo,
                onClickBack = { onClickBack() },
                onClickAuthorProfileImage = { onClickAuthorProfileImage() },
                onClickFavorite = { onClickFavorite() },
                onClickShare = { onClickShare() },
                isSavePost = isSavePost,
                onClickSave = { recipeId-> onClickSave(recipeId) },
                onClickModify = { onClickModify() },
                onClickTalkingRecipe = { onClickTalkingRecipe(authorInfo, recipe) }
            )
        }
        is RecipeState.OnError -> {
            val cause = (recipeState as RecipeState.OnError).cause
            ErrorView(cause = cause) { onClickBack() }
        }
    }
}