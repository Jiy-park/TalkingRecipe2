package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteStep
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.Init
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.ModifyMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.ReadMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.WriteMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState.OnConnected
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState.OnError
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState.OnInit
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState.OnStable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/** [RecipeViewModel]의 모드. 현재 레시피에 대해 어떠한 작업을 진행하는지 결정.
 * * [Init]
 * * [ReadMode]
 * * [WriteMode]
 * * [ModifyMode]*/
sealed class RecipeViewModelMode(val name: String){
    /** [RecipeViewModel]이 초기화되어 어떠한 모드도 정해지지 않은 상태.
     * [RecipeViewModel.init]함수의 매개변수로 [Init]이 들어온 경우
     * [RecipeViewModel.state]의 값을 [OnError]로 변경한다.*/
    object Init: RecipeViewModelMode("Init")

    /** 레시피를 읽는 모드.*/
    object ReadMode: RecipeViewModelMode("Read")

    /** 레시피를 새롭게 작성하는 모드.
     * @param step 레시피의 작성 단계.
     * @see RecipeWriteStep*/
    object WriteMode: RecipeViewModelMode("Write"){
        var step = MutableStateFlow<RecipeWriteStep>(RecipeWriteStep.RecipeBasicInfo)
        fun moveToNextStep(){
            val currentStep = step.value.ordinal
            if(currentStep >= RecipeWriteStep.values().last().ordinal) return
            step.value = RecipeWriteStep.values()[currentStep+1]
        }
        fun moveToPrevStep(){
            val currentStep = step.value.ordinal
            if(currentStep <= 0) return
            step.value = RecipeWriteStep.values()[currentStep-1]
        }
    }

    /** 레시피를 수정하는 모드.
     * @param step 레시피의 작성 단계.
     * @see RecipeWriteStep*/
    object ModifyMode: RecipeViewModelMode("Modify"){
        var step = MutableStateFlow<RecipeWriteStep>(RecipeWriteStep.RecipeBasicInfo)
        fun moveToNextStep(){
            val currentStep = step.value.ordinal
            if(currentStep >= RecipeWriteStep.values().last().ordinal) return
            step.value = RecipeWriteStep.values()[currentStep+1]
        }
        fun moveToPrevStep(){
            val currentStep = step.value.ordinal
            if(currentStep <= 0) return
            step.value = RecipeWriteStep.values()[currentStep-1]
        }
    }

    companion object{
        fun nameOf(modeName: String): RecipeViewModelMode {
            return when(modeName){
                "Write" -> { WriteMode }
                "Read" -> { ReadMode }
                "Modify" -> { ModifyMode }
                else -> { Init }
            }
        }
    }
}


/** [RecipeViewModel]의 상태를 나타내는 값.
 * * [OnInit]
 * * [OnStable]
 * * [OnConnected]
 * * [OnError]*/
sealed class RecipeViewModelState{
    /** 뷰모델이 초기화된 상태. 해당 단계에서 [RecipeViewModelMode]를 선택한다.*/
    object OnInit: RecipeViewModelState(){
        init { Log.d("LOG_CHECK", "RecipeViewModelState : OnInit -> init RecipeViewModel.") }
    }
    /** 안정적 상태. 레시피와 관련된 작업 중 어떠한 문제도 발생하지 않은 상태이다.
     * @param msg 전반적인 상태 변경에 대한 정보를 받는다. 예를 들어 [RecipeViewModelMode]변경, [RecipeViewModelState]변경 등이 있다.*/
    class OnStable(msg: String): RecipeViewModelState(){
        init { Log.d("LOG_CHECK", "RecipeViewModelState : OnStable -> $msg") }
    }
    /** 데이터베이스와 연결된 상태. 레시피에 대한 정보를 주고 받는 상태이다.
     * @param msg 데이터 다운에 관한 정보를 받는다.*/
    class OnConnected(msg: String): RecipeViewModelState(){
        init { Log.d("LOG_CHECK", "RecipeViewModelState : OnFetching -> $msg") }
    }
    /** 에러가 발생한 상태. 데이터를 다운 받는 중 에러가 발생한 상태이다.
     * @param msg 에러에 대한 정보를 받는다.*/
    class OnError(msg: String): RecipeViewModelState(){
        init { Log.d("LOG_CHECK", "RecipeViewModelState : OnError -> $msg") }
    }
}

class RecipeViewModel(
    private val recipeFetchRepo: RecipeFetchRepositoryImpl,
    private val recipeUploadRepo: RecipeUploadRepositoryImpl,
): ViewModel() {
    private var _state = MutableStateFlow<RecipeViewModelState>(OnInit)
    val state: StateFlow<RecipeViewModelState> get() = _state.asStateFlow()

    private var _mode = MutableStateFlow<RecipeViewModelMode>(Init)
    val mode: StateFlow<RecipeViewModelMode> get() = _mode.asStateFlow()

    private var _recipe = MutableStateFlow<Recipe>(Recipe.EmptyRecipe)
    val recipe: StateFlow<Recipe> get() = _recipe.asStateFlow()

    private lateinit var authorInfo: SimpleUserInfo

    /** [RecipeViewModelState] == [Init]일 때만 호출 가능. 그 외에는 작동하지 않는다.
     * @param userInfo [RecipeViewModelMode] == [WriteMode]일 때 [Recipe.authorInfo]를 작성하기 위한 값.
     * @param recipeId [RecipeViewModelMode] == [ReadMode] 또는 [ModifyMode]일 때 레시피의 정보를 다운 받기 위해 필요한 값.
     * [WriteMode]인 경우 어떠한 값도 입력하지 않는다.
     * @param mode [RecipeViewModel]이 처음 초기화 된 후 어떠한 작업을 할지 정함. */
    fun init(
        userInfo: SimpleUserInfo,
        recipeId: String = "",
        mode: RecipeViewModelMode
    ){
        if(_state.value !is OnInit) { return }

        authorInfo = userInfo
        when (mode) {
            is Init -> { _state.value = OnError("init()::RecipeViewModelMode is already in Init.") }
            else -> {
                _state.value = OnStable("init()::RecipeViewModelMode is on stable.\n" +
                        "user info -> $userInfo.\n" +
                        "mode -> ${mode.name}.\n" +
                        "recipe id -> $recipeId.")
                changeMode(mode)
            }
        }

        if(recipeId.isNotBlank() && (_mode.value is ModifyMode || _mode.value is ReadMode)){
            fetchRecipe(recipeId)
        }
    }

    /** 레시피의 정보를 [recipe]로 업데이트한다.
     * 해당 함수는 [WriteMode] 또는 [ModifyMode]에서만 작동한다.*/
    fun updateRecipe(recipe: Recipe){
        if(_mode.value is WriteMode || _mode.value is ModifyMode){
            _recipe.value = recipe
        }
    }

    /** [_mode]의 값을 매개변수로 받은 [mode]로 변경한다.
     * @see RecipeViewModelMode*/
    fun changeMode(mode: RecipeViewModelMode){
        val before = _mode.value
        _mode.value = mode
        _state.value = OnStable("changeMode()::changed mode ${before.name} -> ${mode.name}")
    }

    private fun createRecipeId(): String{
        val format = DateTimeFormatter.ofPattern("yyMMdd_HHmm")
        val createdAt = LocalDateTime.now().format(format)
        val authorId = authorInfo.userId
        val recipeId = "${authorId}_$createdAt"
        _state.value = OnStable("createRecipeId()::recipe id is created. author id -> $authorId. recipe id -> $recipeId")
        return recipeId
    }

    private fun fetchRecipe(recipeId: String){
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _state.value = OnConnected("fetchRecipe()::start fetching recipe. recipe id -> $recipeId")
                with(recipeFetchRepo) {
                    val basicInfo = async {
                        _state.value = OnConnected("fetchRecipe()::start fetching recipe basic info")
                        fetchRecipeBasicInfoById(recipeId)
                    }

                    val ingredientList = async {
                        _state.value = OnConnected("fetchRecipe()::start fetching recipe ingredient list")
                        fetchRecipeIngredientListById(recipeId)
                    }

                    val stepInfoList = async {
                        _state.value = OnConnected("fetchRecipe()::start fetching recipe step info list")
                        fetchRecipeStepInfoListById(recipeId)
                    }

                    val thumbnailUri = async {
                        _state.value = OnConnected("fetchRecipe()::start fetching recipe thumbnail image uri")
                        fetchRecipeThumbnailUriById(recipeId)
                    }

                    val authorInfo = async {
                        _state.value = OnConnected("fetchRecipe()::start fetching recipe author info")
                        fetchRecipeAuthorInfo(recipeId)
                    }

                    _recipe.value = Recipe(
                        basicInfo = basicInfo.await(),
                        ingredientList = ingredientList.await(),
                        stepInfoList = stepInfoList.await(),
                        thumbnailUri = thumbnailUri.await(),
                        authorInfo = authorInfo.await()
                    )
                }
                _state.value = OnStable("fetchRecipe()::finished fetching recipe. recipe id -> $recipeId")
            }
        }
        catch (e: Exception){
            _state.value = OnError("fetchRecipe()::fail to fetch recipe data. recipe id -> $recipeId.\nerror -> $e.")
        }
    }

    @Suppress("DeferredResultUnused")
    private fun uploadRecipe(){
        if(_mode.value is WriteMode){
            val recipeId = createRecipeId()
            val updateBasicInfo = _recipe.value.basicInfo
                .copy(
                    recipeId = recipeId,
                    authorId = authorInfo.userId
                )
            _recipe.value = _recipe.value.copy(basicInfo = updateBasicInfo)
        }

        try {
            viewModelScope.launch(Dispatchers.IO) {
                val recipe = _recipe.value
                val recipeId = recipe.basicInfo.recipeId
                with(recipeUploadRepo) {
                    async {
                        _state.value = OnConnected("uploadRecipe()::start upload recipe basic info.")
                        uploadRecipeBasicInfo(recipe.basicInfo)
                    }

                    async {
                        _state.value = OnConnected("uploadRecipe()::start upload recipe ingredient list.")
                        uploadRecipeIngredientList(recipeId, recipe.ingredientList)
                    }

                    async {
                        _state.value = OnConnected("uploadRecipe()::start upload recipe step info list.")
                        uploadRecipeStepInfoList(recipeId, recipe.stepInfoList)
                    }

                    async {
                        _state.value = OnConnected("uploadRecipe()::start upload recipe thumbnail.")
                        uploadRecipeThumbnail(recipeId, recipe.thumbnailUri)
                    }
                }
                _state.value = OnStable("uploadRecipe()::finished upload recipe. recipe id -> $recipeId")
            }
        }
        catch (e: Exception){
            _state.value = OnError("uploadRecipe()::fail to upload recipe data. recipe id -> ${_recipe.value.basicInfo.recipeId}.\nerror -> $e.")
        }
    }
}