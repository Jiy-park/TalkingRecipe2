package com.dd2d.talkingrecipe2.view.create_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGradient
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.ui.theme.matex
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.create_screen.CreateScreenValue.BottomButtonHeight
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeBasicInfo
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeEnd
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeStepInfo
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateRecipeThumbnail
import com.dd2d.talkingrecipe2.view.create_screen.recipe_step.CreateStepMoveButton
import com.dd2d.talkingrecipe2.view_model.CreateState
import com.dd2d.talkingrecipe2.view_model.CreateStep
import com.dd2d.talkingrecipe2.view_model.CreateViewModel


@Composable
fun CreateScreenCenterView(
    modifier: Modifier = Modifier,
    createViewModel: CreateViewModel,
    onClickMoveToMain: ()->Unit,
    onClickMoveToRecipe: () -> Unit
){
    val createState by createViewModel.createState.collectAsState()
    val createStep by createViewModel.createStep.collectAsState()
    if(createState is CreateState.OnFetching){
        LoadingView()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .padding(bottom = BottomButtonHeight)
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
