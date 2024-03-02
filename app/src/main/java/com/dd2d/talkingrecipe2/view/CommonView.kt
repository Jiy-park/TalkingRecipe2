package com.dd2d.talkingrecipe2.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.CommonValue
import com.dd2d.talkingrecipe2.ui.theme.MainText
import com.dd2d.talkingrecipe2.ui.theme.MapleFontFamily
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.ui.theme.matex

@Composable
fun TempView(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    onClick: () -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = color)
    ){
        TextButton(onClick = { onClick() }) {
            kotex(text = label)
        }
    }
}

@Composable
fun LoadingView(
    modifier: Modifier = Modifier
){
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(ImageDecoderDecoder.Factory())
        }
        .build()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.3F))
    ){
        Image(
            painter = rememberAsyncImagePainter(R.drawable.loading_toxi, imageLoader),
            contentDescription = "loading",
            modifier = modifier
                .size(200.dp)
        )
    }
}

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    cause: String,
    onClickBack: () -> Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize().padding(20.dp)
    ){
        matex(text = "에러 발생\n$cause", align = TextAlign.Center, maxLine = Int.MAX_VALUE)
        TextButton(onClick = { onClickBack() }) {
            matex(text = "뒤로가기")
        }
    }
}

/** 앱의 최상단에 보여질 뷰. 뒤로가기 버튼(선택)과 이미지(선택), 텍스로(필수) 구성. 기본적으로 배경 색이 투명하므로 앱의 배경색에 의해 결정됨
 * @param text 앱 상단에 보여질 문자.
 * @param fontColor [text]의 색상
 * @param textLeftImageRes 텍스트 옆 이미지의 id값. null일 경우 이미지가 출력 되지 않음.
 * @param onClickBack 뒤로가기 버튼의 클릭 효과. null일 경우 뒤로가기 버튼이 보이지 않음
 * @param textAlignment 텍스트와 이미지의 정렬 기준. 기본적으로 가운데 정렬되어 있음.
 * @param paddingValue 상단 영역에 패딩 값을 부여*/
@Composable
fun TopView(
    modifier: Modifier = Modifier,
    text: String,
    fontColor:Color,
    textLeftImageRes: Int?,
    onClickBack: (() -> Unit)?,
    textAlignment: Alignment = Alignment.Center,
    paddingValue: PaddingValues = PaddingValues(0.dp),
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValue)
            .height(CommonValue.TopViewHeight)
            .background(color = Color.Transparent)
    ) {
        onClickBack?.let {
            IconButton(
                onClick = { onClickBack() },
                modifier = modifier
                    .align(Alignment.CenterStart)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, tint = MainText, contentDescription = "back arrow for preview screen")
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .wrapContentSize()
                .align(textAlignment)
        ) {
            textLeftImageRes?.let { resId->
                Image(painter = painterResource(id = resId), contentDescription = "top view icon")
            }
            Spacer(modifier = modifier.width(5.dp))
            Text(
                text = text,
                fontFamily = MapleFontFamily,
                fontWeight = FontWeight.Bold,
                color = fontColor,
                fontSize = 20.sp
            )
        }
    }
}