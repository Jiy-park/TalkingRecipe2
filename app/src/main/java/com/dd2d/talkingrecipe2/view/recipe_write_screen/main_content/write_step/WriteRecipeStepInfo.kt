package com.dd2d.talkingrecipe2.view.recipe_write_screen.main_content.write_step

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.ui.CommonValue.StepInfoViewHeight
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.ui.theme.textFieldColor
import com.dd2d.talkingrecipe2.ui.theme.textFieldStyle
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WriteRecipeStepInfo(
    modifier: Modifier = Modifier,
    stepInfoList: List<StepInfo>,
    onChangeStepInfoList: (List<StepInfo>)->Unit,
){
    val context = LocalContext.current
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            val updateList = stepInfoList.toMutableList()
            updateList.apply {
                add(to.index, removeAt(from.index))
            }
            onChangeStepInfoList(updateList)
        }
    )

    var selectedIndex = -1
    val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){ res->
        res?.let { uri->
            val updateList = stepInfoList.toMutableList()
            updateList[selectedIndex] = stepInfoList[selectedIndex].copy(imageUri = uri)
            onChangeStepInfoList(updateList)
        }
    }
    LazyColumn(
        state = state.listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ){
        itemsIndexed(items = stepInfoList, key = { _, info -> info.no }){ index, info ->
            ReorderableItem(reorderableState = state, key = info) { _ ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier
                        .padding(horizontal = 15.dp)
                        .animateItemPlacement()
                ){
                    StepInfoView(
                        context = context,
                        stepExplanation = info.explanation,
                        stepImageUri = info.imageUri,
                        onChangeExplanation = { update->
                            val updateList = stepInfoList.toMutableList()
                            updateList[selectedIndex] = stepInfoList[selectedIndex].copy(explanation = update)
                            onChangeStepInfoList(updateList)
                        },
                        onClickImage = {
                            selectedIndex = index
                            galleryLauncher.launch("image/*")
                        },
                        onClickAdd = {
                            val updateList = stepInfoList.toMutableList()
                            updateList.add(index+1, StepInfo(order = stepInfoList.size+1))
                            onChangeStepInfoList(updateList)
                        },
                        onClickRemove = {
                            val updateList = stepInfoList.toMutableList()
                            updateList.removeAt(index)
                            onChangeStepInfoList(updateList)
                        }
                    )
                    Divider(modifier = modifier.fillMaxWidth(), thickness = 1.dp, color = HintText)
                }
            }
        }
    }
}

@Composable
fun StepInfoView(
    modifier: Modifier = Modifier,
    context: Context,
    stepExplanation: String,
    onChangeExplanation: (String) -> Unit,
    stepImageUri: Uri,
    onClickImage: ()->Unit,
    onClickAdd: ()->Unit,
    onClickRemove: ()->Unit,
){
    val model = if(stepImageUri == Uri.EMPTY) R.drawable.recipe_step_info_default_image else stepImageUri

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .height(StepInfoViewHeight)
        ) {
            TextField(
                value = stepExplanation,
                onValueChange = { onChangeExplanation(it) },
                colors = textFieldColor(removeIndicator = true),
                textStyle = textFieldStyle(size = 15.sp),
                placeholder = { kotex(text = "내용을 입력해주세요.", color = HintText) },
                modifier = modifier
                    .weight(0.6F)
                    .fillMaxHeight()
            )
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(model)
                    .build(),
                contentDescription = "step image",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .weight(0.5F)
                    .aspectRatio(1.6F / 1.2F)
                    .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .clickableWithoutRipple {
                        onClickImage()
                    }
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = Color.White)
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = null, tint = HintText)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .wrapContentHeight()
            ) {
                IconButton(onClick = { onClickAdd() }) {
                    kotex(text = "추가")
                }
                IconButton(onClick = { onClickRemove() }) {
                    kotex(text = "삭제")
                }
            }
        }
    }
}