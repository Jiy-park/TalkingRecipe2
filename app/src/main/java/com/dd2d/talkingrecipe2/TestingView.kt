package com.dd2d.talkingrecipe2

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.IngredientDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfoDTO
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.BasicInfoField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Filter.RecipeIdEqualTo
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingRecipeId
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.kotex
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
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

private const val testId = "TalkingRecipe"
//private val rf = RecipeFetchRepositoryImpl()
//private val ru = RecipeUploadRepositoryImpl(base)
@Composable
@Preview
fun AAA() {

}


sealed class T{
    class A(val name: String): T(){
    }
    object B: T()
}

@Composable
@Preview(showSystemUi = true)
fun UploadImageTest(modifier: Modifier = Modifier) {
    var image by remember { mutableStateOf(Uri.EMPTY) }
    val gallery = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){
        it?.let { uri ->
            image = uri
        }
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val bucket = supabase.storage.from("users_image/Toxi")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ){
        AsyncImage(model = image, contentDescription = "", modifier = modifier.clickable { gallery.launch("image/*") })
        TextButton(
            onClick = {
                scope.launch {
//                    uploadImage(
//                        bucketApi = bucket,
//                        imageUri = image,
//                        uploadPath = image.toImagePath(path = "test", context = context),
//                        onTask = {
//                            it.alog("task")
//                        }
//                    )
                }
            },
            modifier = modifier.fillMaxWidth()
        ) {
            kotex(text = "upload")
        }
        TextButton(
            onClick = {
                scope.launch {
                    image = supabase.storage.from("users_image")
                        .createSignedUrl(path = "1.jpeg", 30.minutes)
                        .toSupabaseUrl()
                        .toUri()
                }
            },
            modifier = modifier.fillMaxWidth()
        ) {
            kotex(text = "fetch")
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showSystemUi = true)
fun MSS(modifier: Modifier = Modifier){
    val speed = 100.dp.value
    var locationX by remember { mutableFloatStateOf(0F) }
    var clickR by remember { mutableStateOf(false) }
    var locationY by remember { mutableFloatStateOf(0F) }
    val interaction = remember { MutableInteractionSource() }
    val isPress by interaction.collectIsPressedAsState()

    while(isPress){
        logging("sad")
    }



    Column(
        modifier = modifier
            .fillMaxSize()
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .weight(1F)
                .graphicsLayer {
                    translationX = locationX
                }
        ){
            Box(
                modifier = modifier
                    .size(200.dp)
                    .background(color = MainColor)
            )

        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
                .weight(0.3F)
                .background(color = Color.Yellow)
                .padding(horizontal = 100.dp)
        ){
            IconButton(
                onClick = { locationX += speed },
                modifier = modifier
                    .align(Alignment.CenterEnd)
                    .size(50.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
            }
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "",
                modifier = modifier
                    .align(Alignment.CenterStart)
                    .size(50.dp)
                    .clickable(
                        indication = rememberRipple(),
                        interactionSource = interaction
                    ) {

                    }
            )
        }
    }
}


@Composable
@Preview(showSystemUi = true)
fun RT(modifier: Modifier = Modifier){

    var trigger by remember { mutableStateOf(false) }
    val y = remember { Animatable(0F) }
    LaunchedEffect(key1 = trigger){
        y.animateTo(
            if(trigger)100F else 0F
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ){
        Box(
            modifier = modifier
                .size(200.dp)
                .background(color = Color.White)
                .graphicsLayer {
                    rotationY = y.value
                }
        ){
            Box(
                modifier = modifier
                    .size(100.dp)
                    .background(color = MainColor)
                    .align(Alignment.TopEnd)
                    .clickable {
                        trigger = !trigger
                    }
                    .zIndex(1F)
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun AniTestingView(modifier: Modifier = Modifier){

    val x = remember { Animatable(0F) }
    var r by remember { mutableStateOf(false) }
    var l by remember { mutableStateOf(false) }

    val scale = remember { Animatable(0F) }

    val roY = remember { Animatable(0F) }
    var ro by remember { mutableStateOf(false) }

    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    val isPress by interactionSource.collectIsPressedAsState()
    LaunchedEffect(key1 = isPress){
        scale.animateTo(
            if(isPress) 1.3F else 1F
        )
        x.animateTo(x.value + 30F)

    }



    LaunchedEffect(key1 = r){
        x.animateTo(x.value + 30F)
    }
    LaunchedEffect(key1 = l){
        x.animateTo(x.value - 30F)
    }
    LaunchedEffect(key1 = ro){
        roY.animateTo(roY.value + 30F)
    }

    isPress.alog("prees")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .weight(1F)
                .graphicsLayer {
                    rotationY = roY.value
                    translationX = x.value
                    scaleX = scale.value
                    scaleY = scale.value
                }
        ){
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .size(200.dp)
                    .background(color = MainColor)
            ){
                kotex(text = "123", color = Color.White)
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
                .weight(1F)
                .background(color = Color.LightGray)
                .padding(15.dp)
        ){
            IconButton(
                onClick = { l = !l },
                modifier = modifier
                    .align(Alignment.CenterStart)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }
            IconButton(
                onClick = { ro = !ro },
                modifier = modifier
                    .align(Alignment.Center)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "")
            }
            IconButton(
                onClick = { r = !r },
                interactionSource = interactionSource,
                modifier = modifier
                    .align(Alignment.CenterEnd)
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
            }
            IconButton(
                onClick = {  },
                interactionSource = interactionSource,
                modifier = modifier
                    .align(Alignment.TopCenter)
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
            }
        }
    }
}

@Composable
@Preview
fun View3(){
    val context = LocalContext.current
    val uri = "https://zuubwaidnjukuunkrlyk.supabase.co/storage/v1/object/sign/recipe_image/TalkingRecipe_240124_1526/step_info/2.jpeg?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cmwiOiJyZWNpcGVfaW1hZ2UvVGFsa2luZ1JlY2lwZV8yNDAxMjRfMTUyNi9zdGVwX2luZm8vMi5qcGVnIiwiaWF0IjoxNzA2MTgxNDgzLCJleHAiOjE3MDY3ODYyODN9.zdiBGsabr8CJVlpBn0ZpT99yz5XE0Ew0QUSs81lXHjM&t=2024-01-25T11%3A18%3A04.718Z"
        .toUri()
    LaunchedEffect(key1 = Unit){
        withContext(Dispatchers.IO){
            try {

                val data = HttpClient().get(uri.toString()).readBytes()

//                val request = ImageRequest.Builder(context).data(uri.toString()).build()
//                val res = ImageLoader(context).execute(request).drawable?.toBitmap()

                supabase.storage.from("users_image").upload(data = data, path = "1.jpeg", upsert = true)
            }
            catch (e: Exception){
                e.alog("error")
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