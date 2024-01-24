package com.dd2d.talkingrecipe2

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri

/** Supabase Storage로부터 받은 결과물을 사용가능한 url로 변환.*/
fun String.toSupabaseUrl() = "${BuildConfig.SUPABASE_URL}/storage/v1/$this"

fun Uri.createThumbnailImagePath(recipeId: String, context: Context): String{
    val type = context.contentResolver.getType(this)?.let{ it.split("/")[1] }?: "jpeg"
    type.alog("thumbnail")
    return "${recipeId}_thumbnail.$type"
}

/** drawable 폴더에 있는 파일의 id( R.drawable.xxx )를 통해 uri 생성*/
fun Int.toUriWithDrawable() = "android.resource://com.dd2d.talkingrecipe2/$this".toUri()
