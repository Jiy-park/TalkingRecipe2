package com.dd2d.talkingrecipe2

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.text.TextUtils.split
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.UploadData
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY
) {
    install(Postgrest)
    install(Storage)
}


@Composable
@Preview(showSystemUi = true)
fun TestingView(
    modifier: Modifier = Modifier
){
    val userId = 1000001
    val userProfilePath = "temp.png"
    var url by remember { mutableStateOf("") }
//    var uriList by remember { mutableStateOf(listOf<Uri>()) }
    val context = LocalContext.current
    var uploadImageUri by remember { mutableStateOf<Uri?>(null) }
    val gallery = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){ res->
        res?.let {
            uploadImageUri = it
            getMimeType(context,it)?.alog("type")
        }
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit){
        loadUserProfileImage(
            userId = userId,
            path = userProfilePath,
            onLoadImageUrl = { url = it },
            onError = { it.alog("fail") }
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Yellow)
    ){
        logging("in")
        AsyncImage(
            model = url,
            contentDescription = "test",
            modifier = modifier.weight(1F)
        )
        AsyncImage(
            model = uploadImageUri,
            contentDescription = "test",
            modifier = modifier.weight(1F)
        )
        TextButton(
            onClick = {
                gallery.launch("image/*")
            }
        ) {
            Text(text = "사진 불러오기")
        }
        TextButton(
            onClick = {
                uploadImageUri?.let { uri->
                    scope.launch{
                        uploadUserProfileImage(
                            context = context,
                            userId = userId,
                            uri = uri,
                            onError = { it.alog("upload error") })
                    }
                }
            }
        ) {
            Text(text = "업로드")
        }
    }
}

suspend fun loadUserProfileImage(
    userId: Int,
    path: String,
    onLoadImageUrl: (url: String) -> Unit,
    onError: (e: Exception)->Unit,
){
    withContext(Dispatchers.IO){
        try {
            BuildConfig.SUPABASE_URL
            val res = supabase.storage.from("users_image/$userId").createSignedUrl(path = "profile/$path", 1.minutes)
            val url = "${BuildConfig.SUPABASE_URL}/storage/v1/$res"
            url.alog("1")
            onLoadImageUrl(url)
        }
        catch (e: Exception){
            onError(e)
        }
    }
}

suspend fun uploadUserProfileImage(
    context: Context,
    userId: Int,
    uri: Uri,
    onError: (e: Exception) -> Unit
){
    withContext(Dispatchers.IO){
        try {
            val type = getMimeType(context, uri)?.let { it.split("/")[1] }?: "jpeg"
            type.alog("type111")
            val imagePath = "${userId}_profile.$type"
            val bucket = supabase.storage.from("users_image/$userId/profile")
            bucket.upload(path = imagePath, uri = uri, upsert = true)
        }catch (e: Exception){
            onError(e)
        }
    }
}

fun getMimeType(context: Context, uri: Uri) = context.contentResolver.getType(uri)
