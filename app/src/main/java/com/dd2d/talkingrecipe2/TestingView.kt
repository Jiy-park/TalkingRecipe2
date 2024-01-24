package com.dd2d.talkingrecipe2

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.IngredientDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfoDTO
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Field.BasicInfoField
import com.dd2d.talkingrecipe2.model.RecipeDBValue.Filter.RecipeIdEqualTo
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingRecipeId
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.io.IOException
import kotlin.time.Duration.Companion.minutes

private val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY
) {
    install(Postgrest)
    install(Storage)
}

@Composable
@Preview
fun View3(){
    LaunchedEffect(key1 = Unit){
        withContext(Dispatchers.IO){
            async {
                fetch1().alog("ok1")
            }
            async{
                fetch2().alog("ok2")
            }
        }
    }
}

suspend fun fetch1(): RecipeBasicInfo{
    logging("1")
    try {
        return withContext(Dispatchers.IO){
            supabase
                .from("recipes")
                .select(columns = Columns.list(BasicInfoField)) {
                    filter {
                        eq(RecipeIdEqualTo, TestingRecipeId)
                    }
                }
                .decodeSingle<RecipeBasicInfoDTO>()
                .toRecipeBasicInfo()
        }
    }
    catch (e: Exception){
        throw IOException("으아아ㅏㅇ아 -> e: $e")
    }
}
suspend fun fetch2(): List<Ingredient>{
    logging("2")
    try {
        return withContext(Dispatchers.IO){
            supabase
                .from("recipe_ingredient")
                .select{
                    filter {
                        eq(RecipeIdEqualTo, TestingRecipeId)
                    }
                }
                .decodeList<IngredientDTO>()
                .map { it.toIngredient() }
        }
    }
    catch (e: Exception){
        throw IOException("으아아ㅏㅇ아 -> e: $e")
    }
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


@SuppressLint("UnrememberedMutableState")
@Composable
@Preview(showSystemUi = true)
fun ComposeTest(
    modifier: Modifier = Modifier
){
    var t by remember { mutableStateOf("") }
    var tt by remember { mutableStateOf("") }
    var ttt by remember { mutableStateOf("") }
    var s by mutableStateOf("")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        TextField(value = tt, onValueChange = { tt = it })
        TextField(value = ttt, onValueChange = { ttt = it })
        View2(t, { t = it }, s, { s = it })
    }


}

@Composable
fun View2(
    t: String,
    onChangeT: (String)->Unit,
    s: String,
    onChangeS: (String)->Unit,
){
        TextField(value = t, onValueChange = { onChangeT(it) })
        TextField(value = s, onValueChange = { onChangeS(it) })

}

@Composable
@Preview(showSystemUi = true)
fun VerticalReorderList() {
    val data = remember { mutableStateOf(List(100) { "Item $it" }) }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        data.value = data.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    })
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
    ) {
        items(items = data.value) { item ->
            ReorderableItem(state, key = item) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "")
                Column(
                    modifier = Modifier
                        .shadow(elevation.value)
                        .background(Color.LightGray)
                ) {
                    Text(
                        text = item,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .height(30.dp)
                    )
                }
            }
        }
    }
}