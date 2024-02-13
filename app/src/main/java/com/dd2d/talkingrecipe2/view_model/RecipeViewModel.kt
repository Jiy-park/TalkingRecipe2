package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.RecipePost
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.logging
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.ModifyMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.OnModeError
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
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/** 레시피에 대한 유저의 관심도를 나타내는 데이터의 집합체.
 * 항목으로는 아래와 같다.
 * * [isFavorite] 유저가 레시피의 좋아요(하트 버튼)을 눌렀는지에 대한 정보.
 * * [isSaved] 유저가 레시피를 보관함에 추가했는지에 대한 정보.*/
data class RecipeInterestInfo(
    val isFavorite: Boolean,
    val isSaved: Boolean,
){
    companion object{
        val DefaultInterest = RecipeInterestInfo(
            isFavorite = false,
            isSaved = false
        )
    }
}

/** [RecipeViewModel]의 모드. 현재 레시피에 대해 어떠한 작업을 진행하는지 결정.
 * * [ReadMode]
 * * [WriteMode]
 * * [ModifyMode]
 * * [OnModeError]*/
sealed class RecipeViewModelMode(val name: String){
    /** 레시피를 읽는 모드.*/
    object ReadMode: RecipeViewModelMode("Read")

    /** 레시피를 새롭게 작성하는 모드.*/
    object WriteMode: RecipeViewModelMode("Write")

    /** 레시피를 수정하는 모드.*/
    object ModifyMode: RecipeViewModelMode("Modify")

    /** 모드 선택 중 에러 발생. */
    object OnModeError: RecipeViewModelMode("Error")
    companion object{
        fun nameOf(modeName: String): RecipeViewModelMode {
            return when(modeName){
                "Write" -> { WriteMode }
                "Read" -> { ReadMode }
                "Modify" -> { ModifyMode }
                else -> { OnModeError }
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
    class OnError(val msg: String): RecipeViewModelState(){
        init { Log.d("LOG_CHECK", "RecipeViewModelState : OnError -> $msg") }
    }
}

class RecipeViewModel(
    private val recipeFetchRepo: RecipeFetchRepositoryImpl,
    private val recipeUploadRepo: RecipeUploadRepositoryImpl,
): ViewModel() {
    private var _state = MutableStateFlow<RecipeViewModelState>(OnInit)
    val state: StateFlow<RecipeViewModelState> get() = _state.asStateFlow()

    private var _recipe = MutableStateFlow<Recipe>(Recipe.EmptyRecipe)
    val recipe: StateFlow<Recipe> get() = _recipe.asStateFlow()

    private var _recipeInterestInfo = MutableStateFlow<RecipeInterestInfo>(RecipeInterestInfo.DefaultInterest)
    val recipeInterestInfo: StateFlow<RecipeInterestInfo> get() = _recipeInterestInfo.asStateFlow()

    private lateinit var userInfo: SimpleUserInfo

    /** 레시피의 정보를 [recipe]로 업데이트한다..*/
    fun updateRecipe(recipe: Recipe){
        _recipe.value = recipe
    }

    fun initUser(user: SimpleUserInfo){
        userInfo = user
    }

    init {
        logging("eq")
    }

    private fun createRecipeId(): String{
        val format = DateTimeFormatter.ofPattern("yyMMdd_HHmm")
        val createdAt = LocalDateTime.now().format(format)
        val authorId = userInfo.userId
        val recipeId = "${authorId}_$createdAt"
        _state.value = OnStable("createRecipeId()::recipe id is created. author id -> $authorId. recipe id -> $recipeId")
        return recipeId
    }

    /** [recipeId]에 맞는 레시피의 정보를 모두 불러온다.
     * @see fetchRecipeByPost*/
    fun fetchRecipeById(recipeId: String){
        if(recipeId.isEmpty()) { return }
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _state.value = OnConnected("fetchRecipe()::start fetching recipe. recipe id -> $recipeId")
                with(recipeFetchRepo) {
                    val recipeVersion = withContext(Dispatchers.IO) {
                        _state.value = OnConnected("fetchRecipe()::check recipe version")
                        recipeFetchRepo.checkRecipeVersion(recipeId)
                    }

                    val basicInfo = async {
                        _state.value = OnConnected("fetchRecipe()::start fetching recipe basic info")
                        fetchRecipeBasicInfoById(recipeId)
                    }

                    val ingredientList = async {
                        _state.value = OnConnected("fetchRecipe()::start fetching recipe ingredient list")
                        fetchRecipeIngredientListById(recipeId, recipeVersion)
                    }

                    val stepInfoList = async {
                        _state.value = OnConnected("fetchRecipe()::start fetching recipe step info list")
                        fetchRecipeStepInfoListById(recipeId, recipeVersion)
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

    /** [RecipePost]를 기반으로 하여 레시피의 정보를 다운 받는다.
     * @see fetchRecipeById*/
    fun fetchRecipeByPost(post: RecipePost){
        val basicInfo = post.recipeBasicInfo
        val thumbnailImageUri = post.thumbnailImageUri
        val recipeVersion = basicInfo.version
        val recipeId = basicInfo.recipeId

        try {
            viewModelScope.launch(Dispatchers.IO) {
                _state.value = OnConnected("fetchRecipeByPost()::start fetching recipe. recipe id -> $recipeId")
                with(recipeFetchRepo) {
                    val ingredientList = async {
                        _state.value = OnConnected("fetchRecipeByPost()::start fetching recipe ingredient list")
                        fetchRecipeIngredientListById(recipeId, recipeVersion)
                    }

                    val stepInfoList = async {
                        _state.value = OnConnected("fetchRecipeByPost()::start fetching recipe step info list")
                        fetchRecipeStepInfoListById(recipeId, recipeVersion)
                    }

                    val authorInfo = async {
                        _state.value = OnConnected("fetchRecipeByPost()::start fetching recipe author info")
                        fetchRecipeAuthorInfo(recipeId)
                    }

                    _recipe.value = Recipe(
                        basicInfo = basicInfo,
                        ingredientList = ingredientList.await(),
                        stepInfoList = stepInfoList.await(),
                        thumbnailUri = thumbnailImageUri,
                        authorInfo = authorInfo.await()
                    )
                }
                _state.value = OnStable("fetchRecipeByPost()::finished fetching recipe. recipe id -> $recipeId")
            }
        }
        catch (e: Exception){
            _state.value = OnError("fetchRecipeByPost()::fail to fetch recipe data. recipe id -> $recipeId.\nerror -> $e.")
        }
    }

    @Suppress("DeferredResultUnused")
    fun uploadRecipe(      ){
//        if(_mode.value is WriteMode){
//            val recipeId = createRecipeId()
//            val updateBasicInfo = _recipe.value.basicInfo
//                .copy(
//                    recipeId = recipeId,
//                    authorId = authorInfo.userId
//                )
//            _recipe.value = _recipe.value.copy(basicInfo = updateBasicInfo)
//        }

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

    /** 레시피가 수정되었을 경우 레시피의 버전을 업데이트한다.
     * 버전은 [System.currentTimeMillis]을 사용하여 업데이트 시점으로 한다.
     * * 업데이트 대상
     * * [RecipeBasicInfo]
     * * [List] of [Ingredient]
     * * [List] of [StepInfo]*/
    private fun updateRecipeVersion(){
        val origin = _recipe.value
        val basicInfoOrigin = origin.basicInfo
        val ingredientListOrigin = origin.ingredientList
        val stepInfoListOrigin = origin.stepInfoList

        val version = System.currentTimeMillis()
        _recipe.value = origin.copy(
            basicInfo = basicInfoOrigin.copy(version = version),
            ingredientList = ingredientListOrigin.map { ingredient->
                ingredient.copy(version = version)
            },
            stepInfoList = stepInfoListOrigin.map { stepInfo->
                stepInfo.copy(version = version)
            }
        )
    }
}