package com.dd2d.talkingrecipe2.view.recipe_read_screen.main_content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.ui.CommonValue
import com.dd2d.talkingrecipe2.ui.theme.kotex
import kotlin.math.roundToInt


@Composable
fun RecipeIngredientView(
    modifier: Modifier = Modifier,
    ingredientList: List<Ingredient>,
) {
    val gridHeight =
        ((ingredientList.size/2 + 1) * (CommonValue.IngredientViewHeight.value + CommonValue.IngredientViewVerticalPaddingValue.value))
            .roundToInt().dp

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ){
        kotex(text = "재료", weight = FontWeight.Bold, size = 18.sp)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(CommonValue.IngredientViewVerticalPaddingValue),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = modifier
                .fillMaxWidth()
                .height(gridHeight)
        ){
            items(items = ingredientList){ ingredient->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier
                        .height(CommonValue.IngredientViewHeight)
                ){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        kotex(text = ingredient.name)
                        kotex(text = ingredient.amount)
                    }
                    Divider(modifier = modifier.fillMaxWidth())
                }
            }
        }
    }
}
