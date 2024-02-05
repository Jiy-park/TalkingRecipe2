package com.dd2d.talkingrecipe2.view.recipe_write_screen.write_step

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.Level
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGray
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.ui.theme.textFieldColor
import com.dd2d.talkingrecipe2.ui.theme.textFieldStyle

@Composable
fun WriteRecipeBasicInfo(
    modifier : Modifier = Modifier,
    basicInfo: RecipeBasicInfo,
    onChangeBasicInfo: (RecipeBasicInfo)->Unit,
    ingredientList: List<Ingredient>,
    onChangeIngredientList: (List<Ingredient>) -> Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
            .verticalScroll(state = rememberScrollState())
    ){
        TextField(
            value = basicInfo.title,
            onValueChange = { updateTitle-> onChangeBasicInfo(basicInfo.copy(title = updateTitle)) },
            placeholder = { kotex(text = "레시피 제목", color = HintText, size = 20.sp, weight = FontWeight.Bold) },
            singleLine = true,
            colors = textFieldColor(),
            textStyle = textFieldStyle(size = 20.sp),
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

        TextField(
            value = basicInfo.description,
            onValueChange = { updateDescription-> onChangeBasicInfo(basicInfo.copy(description = updateDescription)) },
            placeholder = { kotex(text = "한 줄 소개", color = HintText) },
            singleLine = true,
            colors = textFieldColor(removeIndicator = true),
            textStyle = textFieldStyle(size = 15.sp),
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

        Divider(modifier = modifier
            .fillMaxWidth())

        QuestionField(
            time = basicInfo.time,
            amount = basicInfo.amount,
            onChangeTime = { updateTime-> onChangeBasicInfo(basicInfo.copy(time = updateTime)) },
            onChangeAmount = { updateAmount-> onChangeBasicInfo(basicInfo.copy(amount = updateAmount)) }
        )

        LevelSelector(
            selectedLevel = basicInfo.level,
            onChangeLevel = { updateLevel-> onChangeBasicInfo(basicInfo.copy(level = updateLevel)) }
        )

        IngredientField(
            ingredientList = ingredientList,
            onChangeIngredient = { index, ingredient ->
                val updatedList = ingredientList.toMutableList()
                updatedList[index] = ingredient
                onChangeIngredientList(updatedList)
            },
            onCLickAdd = {
                val updatedList = ingredientList.toMutableList()
                updatedList.add(Ingredient(no = ingredientList.size+1, version = 0))
                onChangeIngredientList(updatedList)
            },
            onClickRemove = { index->
                val updatedList = ingredientList.toMutableList()
                updatedList.removeAt(index)
                onChangeIngredientList(updatedList)
            }
        )
    }
}

@Composable
fun IngredientField(
    modifier: Modifier = Modifier,
    ingredientList: List<Ingredient>,
    onChangeIngredient: (index: Int, ingredient: Ingredient) -> Unit,
    onCLickAdd: ()->Unit,
    onClickRemove: (index: Int)->Unit,
){
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ){
        kotex(text = "필요한 재료는 어떤 건가요? (예시 : 소금 2ts)", size = 13.sp)
        ingredientList.forEachIndexed { i, ingredient ->
            IngredientInput(
                name = ingredient.name,
                amount = ingredient.amount,
                onChangeName = { updateName->
                    onChangeIngredient(i, ingredient.copy(name = updateName))
                },
                onChangeAmount = { updateAmount->
                    onChangeIngredient(i, ingredient.copy(amount = updateAmount))
                },
                onClickRemove = { onClickRemove(i) }
            )
        }
        Spacer(modifier = modifier.height(20.dp))
        TextButton(
            onClick = { onCLickAdd() },
            modifier = modifier
                .fillMaxWidth()
                .background(color = BackgroundGray, )

        ) {
            kotex(text = "재료 추가하기")
        }
    }
}

@Composable
fun IngredientInput(
    modifier: Modifier = Modifier,
    name: String,
    amount: String,
    onChangeName: (String)->Unit,
    onChangeAmount: (String)->Unit,
    onClickRemove: ()->Unit,
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        TextField(
            value = name,
            onValueChange = { onChangeName(it) },
            colors = textFieldColor(),
            textStyle = textFieldStyle(size = 13.sp),
            placeholder = { kotex(text = "재료 이름", size = 13.sp, color = HintText) },
            singleLine = true,
            modifier = modifier
                .weight(0.6F)
        )
        TextField(
            value = amount,
            onValueChange = { onChangeAmount(it) },
            colors = textFieldColor(),
            textStyle = textFieldStyle(size = 13.sp),
            placeholder = { kotex(text = "필요양", size = 13.sp, color = HintText) },
            singleLine = true,
            modifier = modifier
                .weight(0.2F)
        )
        IconButton(
            onClick = { onClickRemove() },
            modifier
                .weight(0.1F)
                .aspectRatio(1F/1F)
        ) {
            Icon(imageVector = Icons.Filled.Close, tint = BackgroundGray, contentDescription = "remove ingredient")
        }

    }
}



@Composable
fun QuestionField(
    modifier: Modifier = Modifier,
    time: String,
    onChangeTime: (String)->Unit,
    amount: String,
    onChangeAmount: (String)->Unit,
){
    Spacer(modifier = modifier.height(30.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        QuestionBox(
            question = "얼마나 걸리나요?",
            suffix = "분",
            value = time,
            onChangeValue = { onChangeTime(it) },
            modifier = modifier.weight(1F)
        )
        QuestionBox(
            question = "양은 얼마나 되나요?",
            suffix = "인분",
            value = amount,
            onChangeValue = { onChangeAmount(it) },
            modifier = modifier.weight(1F)
        )
    }
}

@Composable
fun QuestionBox(
    modifier: Modifier = Modifier,
    question: String,
    suffix: String,
    value: String,
    onChangeValue: (String)->Unit,
){
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ){
        kotex(text = question, size = 13.sp)
        TextField(
            value = value,
            onValueChange = { onChangeValue(it) },
            shape = RoundedCornerShape(10.dp),
            suffix = { kotex(text = suffix) },
            colors = textFieldColor(backgroundColor = HintText, removeIndicator = true),
            textStyle = textFieldStyle(size = 15.sp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

    }
}

@Composable
fun LevelSelector(
    modifier: Modifier = Modifier,
    selectedLevel: Level,
    onChangeLevel: (Level)->Unit,
){
    Spacer(modifier = modifier.height(30.dp))
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ){
        kotex(text = "얼마나 어렵나요?", size = 13.sp)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Level.values().drop(1).forEach { level->
                LevelViewer(
                    level = level,
                    isChecked = selectedLevel == level,
                    onClick = { onChangeLevel(level) },
                    modifier = modifier
                        .weight(1F)
                        .aspectRatio(1F / 1F)
                )
            }
        }
    }
}

@Composable
fun LevelViewer(
    modifier: Modifier = Modifier,
    level: Level,
    isChecked: Boolean,
    onClick: (Level)->Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .wrapContentHeight()
    ){
        Surface(
            shape = CircleShape,
            border = if(isChecked) BorderStroke(3.dp, color = MainColor) else BorderStroke(0.dp, color = Color.Transparent),
            modifier = modifier
                .clickableWithoutRipple {
                    onClick(level)
                }
        ) {
            Image(painter = painterResource(id = level.resId), contentDescription = "level")
        }
        kotex(text = level.description, color = if(isChecked) MainColor else MainText, weight = if(isChecked) FontWeight.Bold else FontWeight.Medium)
    }
}