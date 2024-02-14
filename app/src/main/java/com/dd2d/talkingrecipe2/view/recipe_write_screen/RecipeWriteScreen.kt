package com.dd2d.talkingrecipe2.view.recipe_write_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGradient
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.ui.theme.matex
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState
import com.dd2d.talkingrecipe2.view_model.UserViewModel

@Composable
fun RecipeWriteScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel,
    recipeViewModelMode: RecipeViewModelMode,
    moveToRecipe: ()->Unit,
){
    val recipe by recipeViewModel.recipe.collectAsState()
    val state by recipeViewModel.state.collectAsState()

    when(state){
        is RecipeViewModelState.OnConnected -> { LoadingView() }
        is RecipeViewModelState.OnStable -> {
            if((state as RecipeViewModelState.OnStable).onEnd){
                EndWrite(
                    onClickMoveToRecipe = { moveToRecipe() },
                    onClickMoveToMain = { navController.navigateUp() },
                )
            }
            else{
                RecipeWriteView(
                    recipe = recipe,
                    onChangeRecipe = { update-> recipeViewModel.updateRecipe(update) },
                    writeMode = recipeViewModelMode,
                    onEndWrite = { recipeViewModel.uploadRecipe(mode = recipeViewModelMode) },
                    onClickBack = { navController.navigateUp() },
                    requestErrorView = {
                        recipeViewModel.requestState(RecipeViewModelState.OnError("value of RecipeViewModelMode is unexpected value"))
                    }
                )
            }
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
private fun EndWrite(
    modifier: Modifier = Modifier,
    onClickMoveToMain: ()->Unit,
    onClickMoveToRecipe: ()->Unit
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = BackgroundGradient)
            .padding(bottom = 50.dp, start = 20.dp, end = 20.dp)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.align(Alignment.Center)
        ){
            matex(
                text = "레시피가 성공적으로\n등록됐어요!",
                color = Color.White,
                weight = FontWeight.Bold,
                align = TextAlign.Center,
                size = 18.sp,
                maxLine = 2
            )
            Image(
                painter = painterResource(id = R.drawable.complete_upload_recipe),
                contentDescription = null,
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .graphicsLayer {
                        scaleX = 0.7F
                        scaleY = 0.7F
                    }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier.align(Alignment.BottomCenter)
        ){
            TextButton(
                onClick = { onClickMoveToMain() },
                modifier = modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(40.dp))
            ) {
                kotex(text = "메인으로 이동하기", weight = FontWeight.Bold)
            }
            TextButton(
                onClick = { onClickMoveToRecipe() },
                modifier = modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(40.dp))
            ) {
                kotex(text = "레시피 확인하기", weight = FontWeight.Bold, color = MainColor)
            }
        }
    }
}