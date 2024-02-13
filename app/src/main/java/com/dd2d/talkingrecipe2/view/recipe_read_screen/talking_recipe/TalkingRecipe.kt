package com.dd2d.talkingrecipe2.view.recipe_read_screen.talking_recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.ui.CommonValue.TalkingRecipeStepImageHeight
import com.dd2d.talkingrecipe2.ui.fillWidthOfParent
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreenTopView

@Composable
fun TalkingRecipe(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onClickBack: ()->Unit,
    onClickAuthor: (author: SimpleUserInfo)->Unit,
    onClickToMain: ()->Unit,
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val stepInfoList = recipe.stepInfoList
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ){
        RecipeReadScreenTopView(modifier = modifier.fillWidthOfParent(15.dp)) { onClickBack() }
        SimpleAuthorInfoView(
            recipeTitle = recipe.basicInfo.title,
            authorInfo = recipe.authorInfo,
            onClickAuthor = { onClickAuthor(recipe.authorInfo) }
        )

        Spacer(modifier = modifier.height(15.dp))

        AsyncImage(
            model = stepInfoList[currentStep].imageUri,
            contentDescription = "recipe step info image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillWidthOfParent(15.dp)
                .height(TalkingRecipeStepImageHeight)
        )

        Spacer(modifier = modifier.height(15.dp))

        StepInfoExplanationView(
            explanation = stepInfoList[currentStep].description,
            modifier = modifier.weight(1F),
            onClickToxi = {  }
        )

        TalkingRecipeStepMoveButton(
            currentStep = currentStep,
            lastStep = stepInfoList.size,
            onClickNext = {
                if(currentStep < stepInfoList.size) { currentStep++ }
                else { onClickToMain() }
            },
            onClickPrev = {
                if(currentStep > 0) { currentStep-- }
                else { onClickBack() }
            },
            modifier = modifier.fillWidthOfParent(15.dp)
        )
    }
}
