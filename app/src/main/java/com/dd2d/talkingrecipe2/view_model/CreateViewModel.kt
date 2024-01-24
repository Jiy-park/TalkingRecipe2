package com.dd2d.talkingrecipe2.view_model

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.createThumbnailImagePath
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.IngredientDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfoDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfoDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.createStepInfoImagePath
import com.dd2d.talkingrecipe2.data_struct.recipe.toDTO
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateState
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateStep
import com.dd2d.talkingrecipe2.navigation.CreateScreenMode
import com.dd2d.talkingrecipe2.toSupabaseUrl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order.ASCENDING
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.minutes

/** @param createScreenMode 레시피 수정 모드 판단. 파라미터로 받은 값이 [CreateScreenMode.Modify]일 경우 레시피 수정 모드*/
class CreateViewModel(
    private val userId: String,
    val createScreenMode: CreateScreenMode,
    application: Application,
): AndroidViewModel(application = application) {
    private lateinit var database: SupabaseClient

    private var _createState = MutableStateFlow<CreateState>(CreateState.Init)
    val createState: StateFlow<CreateState> = _createState.asStateFlow()

    private var _createStep = MutableStateFlow<CreateStep>(CreateStep.RecipeBasicInfo)
    val createStep: StateFlow<CreateStep> = _createStep.asStateFlow()

    var recipeBasicInfo by mutableStateOf(RecipeBasicInfo())
    var thumbnailUri by mutableStateOf<Uri>(Uri.EMPTY)
    var ingredientList = mutableListOf<Ingredient>().toMutableStateList()
    var stepInfoList = mutableListOf<StepInfo>(StepInfo(order = 0)).toMutableStateList()

    private fun createRecipeId(): String{
        val format = DateTimeFormatter.ofPattern("yyMMdd_HHmm")
        val createdAt = LocalDateTime.now().format(format)
        val recipeId = "${userId}_$createdAt"
        Log.d("LOG_CHECK", "createRecipeId()::recipe id is created. author id -> $userId. recipe id -> $recipeId")
        return recipeId
    }

    private fun uploadRecipe(){
        if(createScreenMode is CreateScreenMode.Create){
            recipeBasicInfo.recipeId = createRecipeId()
            recipeBasicInfo.authorId = userId
        }

        val recipeId = recipeBasicInfo.recipeId
        viewModelScope.launch(Dispatchers.IO) {
            _createState.value = CreateState.OnUploading("uploadRecipe()::start uploading recipe. author id -> $userId. recipe id -> $recipeId")

            uploadRecipeBasicInfo()
            uploadIngredientList(recipeId = recipeId)
            uploadStepInfoList(recipeId = recipeId)
            uploadThumbnail(recipeId = recipeId)

            _createState.value = CreateState.Stable("uploadRecipe()::finished uploading recipe. author id -> $userId. recipe id -> $recipeId")
        }
    }

    private suspend fun uploadThumbnail(recipeId: String){
        val context = getApplication<Application>().applicationContext
        try {
            _createState.value = CreateState.OnUploading("uploadThumbnail()::uploading recipe thumbnail image.\n" +
                    "upload -> $thumbnailUri")
            val imagePath = thumbnailUri.createThumbnailImagePath(recipeId = recipeId, context = context)
            database.storage.from("$RecipeImageTable/$recipeId").upload(uri = thumbnailUri, path = imagePath, upsert = true)
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("uploadThumbnail()::fail to uploading recipe thumbnail -> $thumbnailUri\nmessage -> $e")
        }
    }

    private suspend fun uploadStepInfoList(recipeId: String){
        val context = getApplication<Application>().applicationContext
        val bucket = database.storage.from("$RecipeImageTable/$recipeId/$StepInfoImageTable")
        val uploadData = stepInfoList.mapIndexed { order, stepInfo->
            var imagePath = ""
            if(stepInfo.imageUri != Uri.EMPTY){
                imagePath = stepInfo.imageUri.createStepInfoImagePath(order = order, context = context)
                try{
                    _createState.value = CreateState.OnUploading("uploadStepInfoList()::uploading recipe step info image.\n" +
                            "upload -> ${stepInfo.imageUri}")
                    bucket.upload(uri = stepInfo.imageUri, path = imagePath, upsert = true)
                }
                catch (e: Exception){
                    _createState.value = CreateState.OnError("uploadStepInfoList()::fail to upload recipe step info image -> ${stepInfo.imageUri}\nmessage -> $e")
                }
            }
            stepInfo.toDTO(recipeId = recipeId, order = order, imagePath = imagePath)
        }

        try {
            _createState.value = CreateState.OnUploading("uploadStepInfoList()::uploading recipe step info list.\n" +
                    "upload -> $uploadData")
            database.from(StepInfoTable).upsert(uploadData, onConflict = StepInfoUpsertField)
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("uploadStepInfoList()::fail to upload recipe step info list -> $stepInfoList\nmessage -> $e")
        }
    }

    private suspend fun uploadIngredientList(recipeId: String){
        val uploadData = ingredientList.toList().toDTO(recipeId)
        try{
            _createState.value = CreateState.OnUploading("uploadIngredientList()::uploading recipe ingredient list.\n" +
                    "upload -> $uploadData")
            database.from(IngredientTable).upsert(uploadData, onConflict = IngredientUpsertField)
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("uploadIngredientList()::fail to upload recipe ingredient list -> $ingredientList\nupload -> $uploadData\nmessage -> $e")
        }
    }

    private suspend fun uploadRecipeBasicInfo(){
        val uploadData = recipeBasicInfo.toDTO()
        try{
            _createState.value = CreateState.OnUploading("uploadRecipeBasicInfo()::uploading recipe basic info.\n" +
                    "upload -> $uploadData")
            database.from(RecipeTable).upsert(value = uploadData, onConflict = RecipeBasicInfoUpsertField)
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("uploadRecipeBasicInfo()::fail to upload recipe basic info -> $recipeBasicInfo\nupload -> $uploadData\nmessage -> $e")
        }
    }

    fun moveToNextStep(){
        val currentStep = _createStep.value.step
        if(currentStep >= CreateStep.values().last().step) return
        _createStep.value = CreateStep.values()[currentStep+1]
        if(_createStep.value == CreateStep.EndCreate){
            uploadRecipe()
        }
    }

    fun moveToPrevStep(){
        val currentStep = _createStep.value.step
        if(currentStep <= 0) return
        _createStep.value = CreateStep.values()[currentStep-1]
    }

    fun init(){
        database = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ){
            install(Postgrest)
            install(Storage)
        }

        if(createScreenMode is CreateScreenMode.Modify) {
            fetchRecipe(createScreenMode.recipeId)
        }
        else{
            _createState.value = CreateState.Stable("initDatabase()::connect database.")
        }
    }

    private fun fetchRecipe(recipeId: String){
        viewModelScope.launch(Dispatchers.IO) {
            fetchRecipeById(recipeId = recipeId)
        }
    }

    private suspend fun fetchRecipeById(recipeId: String){
        _createState.value = CreateState.OnFetching("fetchRecipeById()::start fetch recipe data. recipe id -> $recipeId")

        fetchRecipeBasicInfoById(recipeId = recipeId){ res->
            recipeBasicInfo = res
        }

        fetchIngredientListById(recipeId = recipeId){ res->
            ingredientList = res.toMutableStateList()
        }

        fetchStepInfoListById(recipeId = recipeId){ res->
            stepInfoList = res.toMutableStateList()
        }

        fetchThumbnailByPath(recipeId = recipeId) { thumbnailUrl->
            thumbnailUri = Uri.parse(thumbnailUrl)
        }

        _createState.value = CreateState.Stable("fetchRecipeById()::finished fetching recipe data. recipe id -> $recipeId")
    }

    /** [recipeId]에 맞는 레시피의 기본 정보를 받아옴.*/
    private suspend fun fetchRecipeBasicInfoById(
        recipeId: String,
        onLoadData: (recipe: RecipeBasicInfo) -> Unit,
    ){
        val columns = Columns.list("recipe_id", "author_id", "title", "description", "level", "time", "amount", "share_option", "calorie")
        try {
            _createState.value = CreateState.OnFetching("fetchRecipeBasicInfoById()::start fetch recipe basic info. recipe id -> $recipeId")
            val recipeBasicInfo = database.from(RecipeTable)
                .select(columns = columns){
                    filter{
                        eq(RecipeIdEqualTo, recipeId)
                    }
                }
                .decodeSingle<RecipeBasicInfoDTO>()
                .toRecipeBasicInfo()
            onLoadData(recipeBasicInfo)
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("fetchRecipeBasicInfoById()::fail to fetch recipe basic info : recipe id -> $recipeId, massage -> ${e.message}")
        }
    }

    /** [recipeId]에 맞는 레시피의 썸네일을 url 형태로 받아옴*/
    private suspend fun fetchThumbnailByPath(
        recipeId: String,
        onLoadData: (url: String) -> Unit
    ){
        try {
            _createState.value = CreateState.OnFetching("fetchThumbnailByPath()::start fetch recipe thumbnail. recipe id -> $recipeId")
            withContext(Dispatchers.IO){
                val res = database.storage.from("$RecipeImageTable/$recipeId").createSignedUrl(path = "${recipeId}_thumbnail.jpeg", ExpiresIn)
                onLoadData(res.toSupabaseUrl())
            }
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("fetchThumbnailByPath():: fail to fetch recipe thumbnail : recipe id -> $recipeId, message -> ${e.message}")
        }
    }

    /** 레시피의 각 단계 정보를 받아옴,
     * 1. 문자로 이루어진 데이터부터 받아옴 = [StepInfoDTO].
     * 2. 받아온 데이터로부터 imagePath를 얻고, [recipeId]와 조합하여 각 단계의 이미지를 받아옴.*/
    private suspend fun fetchStepInfoListById(
        recipeId: String,
        onLoadData: (List<StepInfo>)->Unit,
    ){
        try {
            _createState.value = CreateState.OnFetching("fetchStepInfoListById()::start fetch recipe step info list. recipe id -> $recipeId")
            withContext(Dispatchers.IO){
                val stepInfo = database.from(StepInfoTable)
                    .select {
                        filter {
                            eq(RecipeIdEqualTo, recipeId)
                        }
                        order(OrderBy, ASCENDING)
                    }
                    .decodeList<StepInfoDTO>()

                try {
                    _createState.value = CreateState.OnFetching("fetchStepInfoListById()::start fetch recipe step info image list. recipe id -> $recipeId")
                    val res = stepInfo.map { dto->
                        val imageUri = database
                            .storage
                            .from("$RecipeImageTable/$recipeId/$StepInfoImageTable")
                            .createSignedUrl(path = dto.imagePath, expiresIn = ExpiresIn)
                            .toSupabaseUrl()
                            .toUri()
                        dto.toStepInfo(imageUri = imageUri)
                    }

                    onLoadData(res)
                }
                catch (e: Exception){
                    _createState.value = CreateState.OnError("fetchStepInfoListById():: fail to fetch recipe step info image list : recipe id -> $recipeId, message -> ${e.message}")
                }
            }
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("fetchStepInfoListById():: fail to fetch recipe step info list : recipe id -> $recipeId, message -> ${e.message}")
        }
    }

    /** [recipeId]에 맞는 레시피의 재료 정보를 받아옴.*/
    private suspend fun fetchIngredientListById(
        recipeId: String,
        onLoadData: (List<Ingredient>)->Unit
    ){
        try {
            _createState.value = CreateState.OnFetching("fetchIngredientListById()::start fetch recipe ingredient list. recipe id -> $recipeId")
            withContext(Dispatchers.IO){
                val res = database.from(IngredientTable)
                    .select {
                        filter {
                            eq(RecipeIdEqualTo, recipeId)
                        }
                    }
                    .decodeList<IngredientDTO>()
                    .map { it.toIngredient() }
                onLoadData(res)
            }
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("fetchIngredientListById():: fail to fetch recipe ingredient list : recipe id -> $recipeId, message -> ${e.message}")
        }
    }

    private companion object {
        const val RecipeIdEqualTo = "recipe_id"
        const val RecipeTable = "recipes"
        const val IngredientTable = "recipe_ingredient"
        const val StepInfoTable = "recipe_step_info"
        const val RecipeImageTable = "recipe_image"
        const val StepInfoImageTable = "step_info"
        const val OrderBy = "order"
        const val RecipeBasicInfoUpsertField = "recipe_id"
        const val IngredientUpsertField = "no,recipe_id"
        const val StepInfoUpsertField = "no,recipe_id"
        val ExpiresIn = 30.minutes
    }
}
