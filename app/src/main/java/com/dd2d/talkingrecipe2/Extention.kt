package com.dd2d.talkingrecipe2

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo

/** Supabase Storage로부터 받은 결과물을 사용가능한 url로 변환.*/
fun String.toSupabaseUrl() = "${BuildConfig.SUPABASE_URL}/storage/v1/$this"

fun Uri.createThumbnailImagePath(recipeId: String, context: Context): String{
    val type = context.contentResolver.getType(this)?.let{ it.split("/")[1] }?: "jpeg"
    type.alog("thumbnail")
    return "${recipeId}_thumbnail.$type"
}

/** drawable 폴더에 있는 파일의 id( R.drawable.xxx )를 통해 uri 생성*/
fun Int.toUriWithDrawable() = "android.resource://com.dd2d.talkingrecipe2/$this".toUri()

/** 리스트의 내용물 중 빈 내용물을 필터링 함.
 * @return 이름 또는 양 중 하나라도 공백인 요소를 제외한 모든 아이템.*/
fun List<Ingredient>.removeEmptyElement(dummy: Any? = null) = this.filterNot { ingredient-> ingredient.isEmpty() }

/** 리스트의 내용물 중 빈 내용물을 필터링 함.
 * @return 이름 또는 양 중 하나라도 공백인 요소를 제외한 모든 아이템.*/
fun List<StepInfo>.removeEmptyElement() = this.filterNot { stepInfo-> stepInfo.isEmpty() }