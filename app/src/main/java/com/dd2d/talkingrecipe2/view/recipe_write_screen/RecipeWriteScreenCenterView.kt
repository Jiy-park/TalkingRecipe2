package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ){
        when(writeStep){
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
