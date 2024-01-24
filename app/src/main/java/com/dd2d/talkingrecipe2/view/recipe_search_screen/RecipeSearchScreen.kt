package com.dd2d.talkingrecipe2.view.recipe_search_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.ui.theme.kotex

@Composable
fun RecipeSearchScreen(
    modifier: Modifier = Modifier,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        kotex(text = Screen.RecipeSearch.route)
    }
}