package com.dd2d.talkingrecipe2.view_model

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.data_struct.Level
import com.dd2d.talkingrecipe2.data_struct.RecipeDTO
import com.dd2d.talkingrecipe2.logging
import com.dd2d.talkingrecipe2.navigation.CreateScreenMode
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.minutes

/**
 *- [Init] - [CreateViewModel]의 초기화.
 *- [StartFetch] - 레시피 관련 정보를 다운 중.
 *- [EndFetch] - 레시피를 다운 완료함.
 *- [Stable] - 특별한 상태가 없음.
 *- [OnError] - 레시피 다운 중 에러 발생*/
sealed class CreateState {
    object Init: CreateState(){
        init { Log.d("LOG_CHECK", "Create State : Init -> initial view model") }
    }
    class StartFetch(msg: String): CreateState(){
        init { Log.d("LOG_CHECK", "Create State : StartFetch -> $msg") }
    }
    class EndFetch(recipeId: String): CreateState(){
        init { Log.d("LOG_CHECK", "Create State : EndFetch -> finished fetch recipe. recipe id : $recipeId") }
    }
    class Stable(msg: String): CreateState(){
        init { Log.d("LOG_CHECK", "Create State : Stable -> $msg") }
    }
    class OnError(cause: String): CreateState(){
        init { Log.e("LOG_CHECK", "Create State : OnError -> $cause") }
    }
}

enum class CreateStep(val step: Int) {
    RecipeBasicInfo(step = 0),
    RecipeStepInfo(step = 1),
    RecipeThumbnail(step = 2),
    EndCreate(step = 3),
}

/** @param createScreenMode 레시피 수정 모드 판단. 파라미터로 받은 값이 [CreateScreenMode.Modify]일 경우 레시피 수정 모드*/
class CreateViewModel(val createScreenMode: CreateScreenMode): ViewModel() {
    private lateinit var database: SupabaseClient

    private var _createState = MutableStateFlow<CreateState>(CreateState.Init)
    val createState: StateFlow<CreateState> = _createState.asStateFlow()

    private var _createStep = MutableStateFlow<CreateStep>(CreateStep.RecipeBasicInfo)
    val createStep: StateFlow<CreateStep> = _createStep.asStateFlow()


    var recipeBasicInfo = RecipeBasicInfo()
//    var recipeBasicInfo by mutableStateOf(RecipeBasicInfo())
    var thumbnailUri by mutableStateOf(Uri.EMPTY)
//    var ingredientList by mutableStateOf<List<Ingredient>>(listOf(Ingredient()))
//    var stepInfoList by mutableStateOf<List<StepInfo>>(listOf(StepInfo()))

    var ingredientList = mutableListOf<Ingredient>(
//        Ingredient("1","1"),
//        Ingredient("2","2"),
//        Ingredient("3","3"),
//        Ingredient("4","4"),
//        Ingredient("5","5"),
//        Ingredient("6","6"),
    ).toMutableStateList()
    var stepInfoList = mutableListOf<StepInfo>().toMutableStateList()

    fun test(s: String){
        logging(s)
    }

    init {
        if(_createState.value == CreateState.Init){
            initDatabase()
        }
    }

    fun moveToNextStep(){
        val currentStep = _createStep.value.step
        if(currentStep >= CreateStep.values().last().step) return
        _createStep.value = CreateStep.values()[currentStep+1]
    }

    fun moveToPrevStep(){
        val currentStep = _createStep.value.step
        if(currentStep <= 0) return
        _createStep.value = CreateStep.values()[currentStep-1]
    }

    private fun initDatabase(){
        database = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ){
            install(Postgrest)
            install(Storage)
        }

        _createState.value = CreateState.Stable("initDatabase()::connect database.")
        if(createScreenMode is CreateScreenMode.Modify) {
            fetchModifyRecipe(createScreenMode.recipeId)
        }
    }

    private fun fetchModifyRecipe(recipeId: String){
        viewModelScope.launch(Dispatchers.IO) {
            fetchRecipeById(recipeId = recipeId)
        }
    }

    private suspend fun fetchRecipeById(recipeId: String){
        _createState.value = CreateState.StartFetch("fetchRecipeById()::start fetch recipe data. recipe id -> $recipeId")

        fetchRecipeBasicInfoById(recipeId = recipeId){ res->
            recipeBasicInfo = RecipeBasicInfo(
                recipeId = res.recipeId,
                authorId = res.authorId,
                title = res.title,
                description = res.description,
                level = Level.values()[res.level],
                shareOption = ShareOption.values()[res.shareOption],
                time = res.time.toString(),
                amount = res.amount.toString(),
                calorie = res.calorie.toString(),
            )
        }

        fetchThumbnailByPath(recipeId = recipeId) { thumbnailUrl->
            thumbnailUri = Uri.parse(thumbnailUrl)
        }

        _createState.value = CreateState.EndFetch("fetchRecipeById()::end fetch recipe data. recipe id -> $recipeId")
    }

    /** [recipeId]에 맞는 레시피의 기본 정보를 받아옴.*/
    private suspend fun fetchRecipeBasicInfoById(
        recipeId: String,
        onLoadData: (recipe: RecipeDTO) -> Unit,
    ){
        try {
            _createState.value = CreateState.StartFetch("fetchRecipeBasicInfoById()::start fetch recipe basic info. recipe id -> $recipeId")
            val recipeBasicInfo = database.from("recipes")
                .select{
                    filter{
                        eq("recipe_id", recipeId)
                    }
                }
                .decodeSingle<RecipeDTO>()
            onLoadData(recipeBasicInfo)
            _createState.value = CreateState.Stable("fetchRecipeBasicInfoById()::end fetch recipe basic info. recipe id -> $recipeId")
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("fetchRecipeBasicInfoById()::fail to fetch recipe basic info : recipe id -> $recipeId, massage -> ${e.message}")
        }
    }

    /** [recipeId]에 맞는 레시피의 썸네일을 url 형태로 받아옴*/
    private suspend fun fetchThumbnailByPath(
        recipeId: String,
        onLoadThumbnail: (url: String) -> Unit
    ){
        try {
            _createState.value = CreateState.StartFetch("fetchThumbnailByPath()::start fetch recipe thumbnail. recipe id -> $recipeId")
            withContext(Dispatchers.IO){
                val res = database.storage.from("recipe_image/$recipeId").createSignedUrl(path = "${recipeId}_thumbnail.jpeg", 30.minutes)
                val url = "${BuildConfig.SUPABASE_URL}/storage/v1/$res"
                onLoadThumbnail(url)
                _createState.value = CreateState.Stable("fetchThumbnailByPath()::end fetch recipe thumbnail. recipe id -> $recipeId")
            }
        }
        catch (e: Exception){
            _createState.value = CreateState.OnError("fetchThumbnailByPath():: fail to fetch recipe thumbnail : recipe id -> $recipeId, message -> ${e.message}")
        }
    }
}

data class RecipeBasicInfo(
    var recipeId: String = "",
    val authorId: String = "",
    var title: String = "",
    var description: String = "",
    var level: Level = Level.Normal,
    var time: String = "",
    var amount: String = "",
    var calorie: String = "",
    var shareOption: ShareOption = ShareOption.All
)

data class Ingredient(
    val name: String = "",
    val amount: String = "",
)

@Serializable
data class IngredientDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("recipe_id")
    val recipeId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("amount")
    val amount: String,
)

data class StepInfo(
    val imageUri: Uri? = null,
    val explanation: String = "",
)

@Serializable
data class StepInfoDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("recipe_id")
    val recipeId: Int,
    @SerialName("step_image_url")
    val imageUrl: String,
    @SerialName("explanation")
    val explanation: String = "",
)

enum class ShareOption(val description: String){
    NotShare("나만 공개"), Friends("친구 공개"), All("모두 공개")
}

