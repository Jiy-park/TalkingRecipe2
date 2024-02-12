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
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.ui.CommonValue
import com.dd2d.talkingrecipe2.ui.fillWidthOfParent
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreenBottomView
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreenTopView


@Composable
fun RecipeReadView(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    authorInfo: SimpleUserInfo,
    onClickBack: () -> Unit,
    onClickAuthorProfileImage: () -> Unit,
    onClickFavorite: () -> Unit,
    onClickShare: () -> Unit,
    isSavePost: Boolean,
    onClickSave: (recipeId: String) -> Unit,
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
                authorInfo = authorInfo,
                recipeTitle = recipe.basicInfo.title,
                recipeDescription = recipe.basicInfo.description,
                onClickAuthor = { onClickAuthorProfileImage() },
                onClickFavorite = { onClickFavorite() },
                onClickShare = { onClickShare() },
                isSavePost = isSavePost,
                onClickSave = { onClickSave(recipe.basicInfo.recipeId) },
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