package com.dd2d.talkingrecipe2.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.ui.theme.matex

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
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ){
        matex(text = "에러 발생\n$cause")
    }
}