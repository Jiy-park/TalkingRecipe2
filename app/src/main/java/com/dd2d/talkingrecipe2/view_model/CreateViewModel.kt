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
import com.dd2d.talkingrecipe2.toSupabaseUrl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order.ASCENDING
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
 *- [OnFetching] - 레시피 관련 정보를 다운 중.
 *- [EndFetching] - 레시피를 다운 완료함. 레시피 수정 모드에서만 진입 가능. 해당 상태에서 다운 받은 레시피의 정보를 뷰 코드애서 가져감.
 *- [Stable] - 특별한 상태가 없음.
 *- [OnError] - 레시피 다운 중 에러 발생*/
sealed class CreateState {
    object Init: CreateState(){
        init { Log.d("LOG_CHECK", "Create State : Init -> initial view model") }
    }
    class OnFetching(msg: String): CreateState(){
        init { Log.d("LOG_CHECK", "Create State : OnFetching -> $msg") }
    }
    class EndFetching(msg: String): CreateState(){
        init { Log.d("LOG_CHECK", "Create State : EndFetching -> $msg") }
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

    var recipeBasicInfo by mutableStateOf(RecipeBasicInfoDTO())
    var thumbnailUri by mutableStateOf<Uri>(Uri.EMPTY)
    var ingredientList = mutableListOf<Ingredient>().toMutableStateList()
    var stepInfoList = mutableListOf<StepInfo>(
        StepInfo(0, 0, "1", Uri.EMPTY),
        StepInfo(1, 1, "2", Uri.EMPTY),
        StepInfo(2, 2, "3", Uri.EMPTY),
        StepInfo(3, 3, "4", Uri.EMPTY),
        StepInfo(4, 4, "5", Uri.EMPTY),
        StepInfo(5, 5, "6", Uri.EMPTY),
    ).toMutableStateList()

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
        _createState.value = CreateState.OnFetching("fetchRecipeById()::start fetch recipe data. recipe id -> $recipeId")

        fetchRecipeBasicInfoById(recipeId = recipeId){ res->
            recipeBasicInfo = RecipeBasicInfoDTO(
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

        fetchIngredientListById(recipeId = recipeId){ res->
            ingredientList = res.toMutableStateList()
        }

        fetchStepInfoListById(recipeId = recipeId){ res->
            stepInfoList = res.toMutableStateList()
        }

        fetchThumbnailByPath(recipeId = recipeId) { thumbnailUrl->
            thumbnailUri = Uri.parse(thumbnailUrl)
        }

        _createState.value = CreateState.EndFetching("fetchRecipeById()::finished fetching recipe data. recipe id -> $recipeId")
    }

    /** [recipeId]에 맞는 레시피의 기본 정보를 받아옴.*/
    private suspend fun fetchRecipeBasicInfoById(
        recipeId: String,
        onLoadData: (recipe: RecipeDTO) -> Unit,
    ){
        try {
            _createState.value = CreateState.OnFetching("fetchRecipeBasicInfoById()::start fetch recipe basic info. recipe id -> $recipeId")
            val recipeBasicInfo = database.from(RecipeTable)
                .select{
                    filter{
                        eq(RecipeId, recipeId)
                    }
                }
                .decodeSingle<RecipeDTO>()
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
                            eq(RecipeId, recipeId)
                        }
                        order(OrderBy, ASCENDING)
                    }
                    .decodeList<StepInfoDTO>()


                try {
                    _createState.value = CreateState.OnFetching("fetchStepInfoListById()::start fetch recipe step info image list. recipe id -> $recipeId")
                    val res = stepInfo.map { dto->
                        val imageUrl = database
                            .storage
                            .from("$RecipeImageTable/$recipeId/$StepInfoImageTable")
                            .createSignedUrl(path = dto.imagePath, expiresIn = ExpiresIn)
                            .toSupabaseUrl()

                        dto.toStepInfo(imageUrl = imageUrl)
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
                            eq(RecipeId, recipeId)
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

    companion object {
        const val RecipeId = "recipe_id"
        const val RecipeTable = "recipes"
        const val IngredientTable = "recipe_ingredient"
        const val StepInfoTable = "recipe_step_info"
        const val RecipeImageTable = "recipe_image"
        const val StepInfoImageTable = "step_info"
        const val OrderBy = "order"
        val ExpiresIn = 30.minutes
    }
}

data class RecipeBasicInfoDTO(
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
    val no: Int,
    val name: String = "",
    val amount: String = "",
){
    fun toDTO(recipeId: String) = IngredientDTO(
        no = this.no,
        recipeId = recipeId,
        name = this.name,
        amount = this.amount
    )
}

@Serializable
data class IngredientDTO(
    @SerialName("no")
    val no: Int,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("name")
    val name: String,
    @SerialName("amount")
    val amount: String,
){
    fun toIngredient() = Ingredient(
        no = this.no,
        name = this.name,
        amount = this.amount
    )
}

data class StepInfo(
    val no: Long = System.currentTimeMillis(),
    val order: Int,
    val explanation: String = "",
    val imageUri: Uri = Uri.EMPTY,
){
    fun toDTO(recipeId: String) = StepInfoDTO(
        no = this.no,
        recipeId = recipeId,
        order = this.order,
        explanation = this.explanation,
        imagePath = "${this.order}.jpeg"
    )
}


@Serializable
data class StepInfoDTO(
    @SerialName("no")
    val no: Long,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("order")
    val order: Int,
    @SerialName("step_image_path")
    val imagePath: String,
    @SerialName("explanation")
    val explanation: String = "",
){
    fun toStepInfo(imageUrl: String) = StepInfo(
        no = this.no,
        order = this.order,
        explanation = this.explanation,
        imageUri = Uri.parse(imageUrl)
    )
}

enum class ShareOption(val description: String){
    NotShare("나만 공개"), Friends("친구 공개"), All("모두 공개")
}

