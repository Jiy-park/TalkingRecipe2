package com.dd2d.talkingrecipe2.view.create_screen.recipe_step

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.clickableWithoutRipple
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.view_model.CreateViewModel
import com.dd2d.talkingrecipe2.view_model.ShareOption

@Composable
fun CreateRecipeThumbnail(
    modifier: Modifier = Modifier,
    createViewModel: CreateViewModel
){

    val context = LocalContext.current
    val thumbnailImageUriList = createViewModel
        .stepInfoList
        .asSequence()
        .filterNot { it.imageUri == Uri.EMPTY }
        .map { it.imageUri }
        .toList()

    var defaultThumbnail by remember { mutableStateOf(Uri.EMPTY) }
    val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){ res->
        res?.let { uri ->
            if(uri in thumbnailImageUriList){
                Toast.makeText(context, "이미 존재하는 이미지입니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                defaultThumbnail = uri
                createViewModel.thumbnailUri = uri
            }
        }
    }


    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ){
        kotex(text = "대표 이미지를 골라주세요.")
        Spacer(modifier = modifier.height(5.dp))
        LazyRow(
            state = rememberLazyListState(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ){
            item {
                ThumbnailView(
                    uri = defaultThumbnail,
                    isChecked = createViewModel.thumbnailUri == defaultThumbnail,
                    onClick = { galleryLauncher.launch("image/*") },
                )
            }
            itemsIndexed(items = thumbnailImageUriList){ index, item ->
                ThumbnailView(
                    uri = item,
                    isChecked = createViewModel.thumbnailUri == item,
                    onClick = {
                        createViewModel.thumbnailUri = item
                    },
                )
            }
        }

        Spacer(modifier = modifier.height(30.dp))
        kotex(text = "누가 읽을 수 있나요?")
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            ShareOption.values().forEach {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .weight(1F)
                        .height(40.dp)
                        .background(
                            color = if (createViewModel.recipeBasicInfo.shareOption == it) MainColor else HintText,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .clickableWithoutRipple {
                            createViewModel.recipeBasicInfo =
                                createViewModel.recipeBasicInfo.copy(shareOption = it)
                        }
                ){
                    kotex(
                        text = it.description,
                        size = 15.sp,
                        color = Color.White,
                        weight = FontWeight.Bold,
                        align = TextAlign.Center,
                    )
                }
            }
        }
    }
}


@Composable
fun ThumbnailView(
    modifier: Modifier = Modifier,
    uri: Uri,
    isChecked: Boolean,
    onClick: ()->Unit,
){
    val model = if(uri == Uri.EMPTY) R.drawable.recipe_step_info_default_image else uri

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.DarkGray,
        border = BorderStroke(width = if(uri != Uri.EMPTY && isChecked) 3.dp else 0.dp, color = MainColor),
        modifier = modifier
            .width(150.dp)
            .aspectRatio(1.6F / 1.2F)
            .clickableWithoutRipple { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ){
            AsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            if(uri != Uri.EMPTY && isChecked){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .wrapContentSize()
                        .align(Alignment.TopStart)
                        .background(
                            color = MainColor,
                            shape = RoundedCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ){
                    kotex(
                        text = "대표",
                        color = Color.White,
                        size = 13.sp,
                        weight = FontWeight.Bold
                    )
                }
            }
        }
    }
}