package com.dd2d.talkingrecipe2

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Supabase Storage로부터 받은 결과물을 사용가능한 url로 변환.*/
fun String.toSupabaseUrl() = "${BuildConfig.SUPABASE_URL}/storage/v1/$this"

/** 이미지 업로드 시 사용. [Uri]를 imagePath로 바꿈. 이떄 imagePath는 path.tpye 형태.
 * 1. 다운 받은 이미지인지 판단 -> startWith("http").
 * 2. 다운받은 경우 [MimeTypeMap] 통해 확장자 파악.
 * 3. 갤러리로부터 온 경우 [Context.get
 * ContentResolver] 통해 확장자 파악
 * @param path 이미지의 path. 확장자 전에 붙을 경로. -> [path].확장자
 * @param context 이미지의 mime type 추정에 사용*/
fun Uri.toImagePath(path: String, context: Context): String{
    val type = if(this.toString().startsWith("http")){
        MimeTypeMap
            .getFileExtensionFromUrl(this.toString())?.let { extension->
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?.let {
                    it.split("/")[1]
                }?: "jpeg"
            }?: throw Exception("toImagePath()::fail to convert uri to image path. cause extension is null")
    }
    else{
        context
            .contentResolver
            .getType(this)?.let{
                it.split("/")[1]
            }?: "jpeg"
    }
    return "$path.$type"
}

/** [Uri]의 출처를 판별.
 * 1. 서버로부터 받은 이미지의 경우 true 반납. -> uri의 시작이 "http..."
 * 2. 갤러리로부터 받은 이미지의 경우 false 반납*/
fun Uri.isFromServer() = this.toString().startsWith("http")

suspend fun Uri.toByteArray() = withContext(Dispatchers.IO) {
    HttpClient().get(urlString = this@toByteArray.toString()).readBytes()
}

/** drawable 폴더에 있는 파일의 id( R.drawable.xxx )를 통해 uri 생성*/
fun Int.toUriWithDrawable() = "android.resource://com.dd2d.talkingrecipe2/$this".toUri()

/** 리스트의 내용물 중 빈 내용물을 필터링 함.
 * @return 이름 또는 양 중 하나라도 공백인 요소를 제외한 모든 아이템.*/
fun List<Ingredient>.removeEmptyIngredient() = this.filterNot { ingredient-> ingredient.isEmpty() }

/** 리스트의 내용물 중 빈 내용물을 필터링 함.
 * @return 이름 또는 양 중 하나라도 공백인 요소를 제외한 모든 아이템.*/
fun List<StepInfo>.removeEmptyStepInfo() = this.filterNot { stepInfo-> stepInfo.isEmpty() }