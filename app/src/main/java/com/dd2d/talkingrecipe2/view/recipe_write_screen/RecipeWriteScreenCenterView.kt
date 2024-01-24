package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateStep
import com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.write_step.EndWrite
import com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.write_step.WriteRecipeBasicInfo
import com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.write_step.WriteRecipeStepInfo
import com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.write_step.WriteRecipeThumbnail
import com.dd2d.talkingrecipe2.view_model.CreateViewModel


@Composable
fun RecipeWriteScreenCenterView(
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
                    WriteRecipeBasicInfo(createViewModel = createViewModel)
                }
                CreateStep.RecipeStepInfo -> {
                    WriteRecipeStepInfo(createViewModel = createViewModel)
                }
                CreateStep.RecipeThumbnail -> {
                    WriteRecipeThumbnail(createViewModel = createViewModel)
                }
                CreateStep.EndCreate -> {
                    EndWrite(
                        onClickMoveToMain = { onClickMoveToMain() },
                        onClickMoveToRecipe = { onClickMoveToRecipe() }
                    )
                }
            }
        }
    }
}
