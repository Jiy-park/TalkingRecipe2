package com.dd2d.talkingrecipe2.view.create_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateStep
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeBasicInfo
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeEnd
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeStepInfo
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeThumbnail
import com.dd2d.talkingrecipe2.view_model.CreateViewModel


@Composable
fun CreateScreenCenterView(
    modifier: Modifier = Modifier,
    createViewModel: CreateViewModel,
    createStep: CreateStep,
    onClickMoveToMain: ()->Unit,
    onClickMoveToRecipe: () -> Unit
){

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ){
            when(createStep){
                CreateStep.RecipeBasicInfo -> {
                    CreateRecipeBasicInfo(createViewModel = createViewModel)
                }
                CreateStep.RecipeStepInfo -> {
                    CreateRecipeStepInfo(createViewModel = createViewModel)
                }
                CreateStep.RecipeThumbnail -> {
                    CreateRecipeThumbnail(createViewModel = createViewModel)
                }
                CreateStep.EndCreate -> {
                    CreateRecipeEnd(
                        onClickMoveToMain = { onClickMoveToMain() },
                        onClickMoveToRecipe = { onClickMoveToRecipe() }
                    )
                }
            }
        }
    }
}
