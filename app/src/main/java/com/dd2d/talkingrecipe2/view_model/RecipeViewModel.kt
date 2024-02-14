package com.dd2d.talkingrecipe2.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.RecipePost
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.ModifyMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.WriteMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState.OnConnected
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState.OnError
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



class RecipeViewModel(
    private val recipeFetchRepo: RecipeFetchRepositoryImpl,
    private val recipeUploadRepo: RecipeUploadRepositoryImpl,
): ViewModel() {
    private var _state = MutableStateFlow<RecipeViewModelState>(OnStable(msg = "init()::viewModel is on stable."))
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
        _state.value = OnStable(msg = "userInfo -> $userInfo")
    }
    /** 레시피 작성 또는 수정 후 레시피를 확인할 때 [_state] 값을 변경시킴.*/
    fun moveToMain(){
        _state.value = OnStable(msg = "moveToMain()::viedwModel is on stable.")
    }

    /** 뷰 단에서 코드가 진행 중 상태의 변화가 필요할 때 사용. */
    fun requestState(state: RecipeViewModelState){
        _state.value = state
    }

    private fun createRecipeId(): String{
        val format = DateTimeFormatter.ofPattern("yyMMdd_HHmm")
        val createdAt = LocalDateTime.now().format(format)
        val authorId = userInfo.userId
        val recipeId = "${authorId}_$createdAt"
        _state.value = OnStable(msg = "createRecipeId()::recipe id is created. author id -> $authorId. recipe id -> $recipeId")
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
                _state.value = OnStable(msg = "fetchRecipe()::finished fetching recipe. recipe id -> $recipeId")
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
                _state.value = OnStable(msg = "fetchRecipeByPost()::finished fetching recipe. recipe id -> $recipeId")
            }
        }
        catch (e: Exception){
            _state.value = OnError("fetchRecipeByPost()::fail to fetch recipe data. recipe id -> $recipeId.\nerror -> $e.")
        }
    }

    @Suppress("DeferredResultUnused")
    fun uploadRecipe(mode: RecipeViewModelMode){
        when(mode){
            is WriteMode -> { setForNewRecipe() }
            is ModifyMode -> { updateRecipeVersion() }
            else -> {
                _state.value = OnError("uploadRecipe()::value of RecipeViewModelMode is unexpected value")
                return
            }
        }

        try {
            viewModelScope.launch {
                val recipe = _recipe.value
                val recipeId = recipe.basicInfo.recipeId
                with(recipeUploadRepo) {
                    _state.value = OnConnected("uploadRecipe()::start upload recipe basic info.")
                    uploadRecipeBasicInfo(recipe.basicInfo)
                    launch {
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

                    }.join()
                }
                _state.value = OnStable(onEnd = true, msg = "uploadRecipe()::finished upload recipe. recipe id -> $recipeId")
            }
        }
        catch (e: Exception){
            _state.value = OnError("uploadRecipe()::fail to upload recipe data. recipe id -> ${_recipe.value.basicInfo.recipeId}.\nerror -> $e.")
        }
    }

    /** 레시피의 작성이 완료된 후, [uploadRecipe]가 호출되는 시점을 기준으로 레시피의 버전을 업데이트한다.
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

    /** 새롭게 작성된 레시피에 필요한 부분을 채워준다.
     * * 레시피 버전 관리 - [updateRecipeVersion]
     * * [userInfo] = [SimpleUserInfo]
     * 1. [RecipeBasicInfo.recipeId] : [createRecipeId]
     * 2. [RecipeBasicInfo.authorId] : [SimpleUserInfo.userId]
     * 3. [Recipe.authorInfo] : [SimpleUserInfo]사용*/
    private fun setForNewRecipe(){
        val recipeId = createRecipeId()
        val updateBasicInfo = _recipe.value.basicInfo
            .copy(
                recipeId = recipeId,
                authorId = userInfo.userId,
            )
        _recipe.value = _recipe.value.copy(
            basicInfo = updateBasicInfo,
            authorInfo = userInfo
        )
        updateRecipeVersion()
    }
}