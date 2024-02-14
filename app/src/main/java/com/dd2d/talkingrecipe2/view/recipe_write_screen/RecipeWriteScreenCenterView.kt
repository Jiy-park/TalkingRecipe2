package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteStep
import com.dd2d.talkingrecipe2.view.recipe_write_screen.write_step.WriteRecipeBasicInfo
import com.dd2d.talkingrecipe2.view.recipe_write_screen.write_step.WriteRecipeStepInfo
import com.dd2d.talkingrecipe2.view.recipe_write_screen.write_step.WriteRecipeThumbnail


@Composable
fun RecipeWriteScreenCenterView(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onChangeRecipe: (Recipe)->Unit,
    writeStep: RecipeWriteStep,
){
    AnimatedContent(
        modifier = modifier,
        targetState = writeStep, label = "",
        transitionSpec = {
            if(initialState.ordinal < targetState.ordinal){
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            }
            else {
                slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
            }
        }
    ) { step->
        when(step){
            RecipeWriteStep.RecipeBasicInfo -> {
                WriteRecipeBasicInfo(
                    basicInfo = recipe.basicInfo,
                    ingredientList = recipe.ingredientList,
                    onChangeBasicInfo = { update->
                        onChangeRecipe(recipe.copy(basicInfo = update))
                    },
                    onChangeIngredientList = { update->
                        onChangeRecipe(recipe.copy(ingredientList = update))
                    }
                )
            }
            RecipeWriteStep.RecipeStepInfo -> {
                WriteRecipeStepInfo(
                    stepInfoList = recipe.stepInfoList,
                    onChangeStepInfoList = { update->
                        onChangeRecipe(recipe.copy(stepInfoList = update))
                    }
                )
            }
            RecipeWriteStep.RecipeThumbnail -> {
                WriteRecipeThumbnail(
                    basicInfo = recipe.basicInfo,
                    onChangeBasicInfo = { update-> onChangeRecipe(recipe.copy(basicInfo = update)) },
                    stepInfoList = recipe.stepInfoList,
                    thumbnailUri = recipe.thumbnailUri,
                    onChangeThumbnailUri = { update-> onChangeRecipe(recipe.copy(thumbnailUri = update)) }
                )
            }
        }
    }
}
