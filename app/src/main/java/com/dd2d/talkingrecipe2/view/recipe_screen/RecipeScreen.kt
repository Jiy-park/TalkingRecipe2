package com.dd2d.talkingrecipe2.view.recipe_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun RecipeScreen(
    modifier: Modifier = Modifier,
    recipeId: Int,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        kotex(text = Screen.Recipe.route + " " + recipeId)
    }
}