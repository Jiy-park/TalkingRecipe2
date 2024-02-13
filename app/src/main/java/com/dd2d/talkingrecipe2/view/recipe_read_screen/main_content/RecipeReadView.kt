package com.dd2d.talkingrecipe2.view.recipe_read_screen.main_content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.ui.CommonValue
import com.dd2d.talkingrecipe2.ui.fillWidthOfParent
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreenBottomView
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreenTopView


@Composable
fun RecipeReadView(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onClickBack: () -> Unit,
    onClickAuthorProfileImage: (author: SimpleUserInfo) -> Unit,
    isSavePost: Boolean,
    isFavoritePost: Boolean,
    onClickFavorite: (Boolean) -> Unit,
    onClickShare: () -> Unit,
    onClickSave: (Boolean) -> Unit,
    onClickModify: () -> Unit,
    onClickTalkingRecipe: ()->Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
    ){
        RecipeReadScreenTopView { onClickBack() }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 20.dp, alignment = Alignment.Top),
            modifier = modifier
                .fillMaxSize()
                .weight(1F)
                .padding(horizontal = 15.dp)
                .verticalScroll(state = rememberScrollState())
        ){
            AuthorInfoView(
                authorInfo = recipe.authorInfo,
                recipeTitle = recipe.basicInfo.title,
                recipeDescription = recipe.basicInfo.description,
                isSavePost = isSavePost,
                isFavoritePost = isFavoritePost,
                onClickAuthor = { onClickAuthorProfileImage(recipe.authorInfo) },
                onClickFavorite = { onClickFavorite(it) },
                onClickSave = { onClickSave(it) },
                onClickShare = { onClickShare() },
                onClickModify = { onClickModify() }
            )
            AsyncImage(
                model = recipe.thumbnailUri,
                contentDescription = "recipe thumbnail image",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillWidthOfParent(15.dp)
                    .height(CommonValue.RecipeScreenRecipeThumbnailImageHeight)
            )
            RecipeBasicInfoView(basicInfo = recipe.basicInfo)
            RecipeIngredientView(ingredientList = recipe.ingredientList)
            RecipeStepInfoView(stepInfoList = recipe.stepInfoList)
        }
        RecipeReadScreenBottomView {
            onClickTalkingRecipe()
        }
    }
}