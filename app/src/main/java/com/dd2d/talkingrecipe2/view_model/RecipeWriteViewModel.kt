package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteMode
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteStep
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed class RecipeWriteState{
    object Init: RecipeWriteState(){
        init { Log.d("LOG_CHECK", "Write State : Init -> initial view model") }
    }
    class OnFetching(msg: String): RecipeWriteState(){
        init { Log.d("LOG_CHECK", "Write State : OnFetching -> $msg") }
    }
    class OnUploading(msg: String): RecipeWriteState(){
        init { Log.d("LOG_CHECK", "Create State : OnUploading -> $msg") }
    }
    object Stable: RecipeWriteState(){
        init { Log.d("LOG_CHECK", "Recipe State : Stable") }
    }
    object OnEndUploading: RecipeWriteState(){
        init { Log.d("LOG_CHECK", "Recipe State : OnEndUploading -> upload recipe is finished.") }
    }
    class OnError(val cause: String): RecipeWriteState(){
        init { Log.e("LOG_CHECK", "Write State : OnError -> $cause") }
    }
}

/** 레시피에 대한 읽기, 쓰기 작업을 할 때 사용됨.
 * @param userId 레시피를 작성 (또는 수정)을 하는 주체의 아이디.
 * @param writeScreenMode 레시피의 쓰기 작업의 분류. [RecipeWriteMode].
 * @param recipeFetchRepo 레시피를 읽기 위해 정보를 가져옴.
 * @param recipeUploadRepo 레시피를 쓰기 위해 사용됨. 레시피의 정보를 데이터베이스로 업로드.*/
class RecipeWriteViewModel(
    private val userId: String,
    val writeScreenMode: RecipeWriteMode,
    private val recipeUploadRepo: RecipeUploadRepositoryImpl,
    private val recipeFetchRepo: RecipeFetchRepositoryImpl,
): ViewModel() {
    private var _writeState = MutableStateFlow<RecipeWriteState>(RecipeWriteState.Init)
    val writeState: StateFlow<RecipeWriteState> = _writeState.asStateFlow()

    private var _writeStep = MutableStateFlow<RecipeWriteStep>(RecipeWriteStep.RecipeBasicInfo)
    val writeStep: StateFlow<RecipeWriteStep> = _writeStep.asStateFlow()

    private var _recipe = MutableStateFlow(Recipe.EmptyRecipe)
    val recipe: StateFlow<Recipe> = _recipe.asStateFlow()

    fun onChangeRecipe(recipe: Recipe){
        _recipe.value = recipe
    }

    fun init(){
        if(_writeState.value == RecipeWriteState.Init){
            when(writeScreenMode){
                is RecipeWriteMode.Create -> {
                    _recipe.value = Recipe.EmptyRecipe
                    _writeState.value = RecipeWriteState.Stable
                }
                is RecipeWriteMode.Modify -> {
                    val recipeId = writeScreenMode.recipeId
                    fetchRecipe(recipeId)
                }
            }

        }
    }

    /** 레시피 작성이 완료되어 업로드를 시작함.
     * [_writeState]의 값이 [RecipeWriteState.Stable]이면서 [_writeStep]의 값이 [RecipeWriteStep.RecipeThumbnail]일 때만 작동해야 함.*/
    fun endWrite(){
        if(_writeState.value == RecipeWriteState.Stable && _writeStep.value == RecipeWriteStep.RecipeThumbnail){
            uploadRecipe()
        }
    }

    /** 데이터베이스에서 [recipeId]에 맞는 레시피를 가져옴.*/
    private fun fetchRecipe(recipeId: String){
        _writeState.value = RecipeWriteState.OnFetching("start fetch recipe. recipe id -> $recipeId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                with(recipeFetchRepo) {
                    val basicInfo = async {
                        _writeState.value = RecipeWriteState.OnFetching("fetchRecipe()::start fetching recipe basic info")
                        fetchRecipeBasicInfoById(recipeId)
                    }

                    val ingredientList = async {
                        _writeState.value = RecipeWriteState.OnFetching("fetchRecipe()::start fetching recipe ingredient list")
                        fetchRecipeIngredientListById(recipeId, 1L)
                    }

                    val stepInfoList = async {
                        _writeState.value = RecipeWriteState.OnFetching("fetchRecipe()::start fetching recipe step info list")
                        fetchRecipeStepInfoListById(recipeId, 1L)
                    }

                    val thumbnailUri = async {
                        _writeState.value = RecipeWriteState.OnFetching("fetchRecipe()::start fetching recipe thumbnail image uri")
                        fetchRecipeThumbnailUriById(recipeId)
                    }

                    val authorInfo = async {
                        _writeState.value = RecipeWriteState.OnFetching("fetchRecipe()::start fetching recipe author info")
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
                _writeState.value = RecipeWriteState.Stable
            }
            catch (e: Exception){
                _writeState.value = RecipeWriteState.OnError("fetchRecipe()::fail to fetch recipe.\nrecipe id -> $recipeId.\nerror in -> $e.")
            }
        }
    }

    private fun uploadRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            _writeState.value = RecipeWriteState.OnUploading("start upload recipe.")
            if(writeScreenMode is RecipeWriteMode.Create){ /** 업로드할 레시피가 새로 만든 레시피인 경우 레시피 아이디를 할당해준다. [createRecipeId]사용.*/
                val recipeId = createRecipeId()
                val updateBasicInfo = _recipe.value.basicInfo
                    .copy(
                        recipeId = recipeId,
                        authorId = userId
                    )
                _recipe.value = _recipe.value.copy(basicInfo = updateBasicInfo)
            }

            val recipeId = _recipe.value.basicInfo.recipeId

            try {
                recipeUploadRepo.uploadRecipe(
                    recipe = _recipe.value,
                    onChangeUploadState = { msg ->
                        _writeState.value = RecipeWriteState.OnUploading(msg)
                    },
                    onEndUpload = {
                        _writeState.value = RecipeWriteState.OnEndUploading
                    }
                )
            }
            catch (e: Exception){
                _writeState.value = RecipeWriteState.OnError("uploadRecipe()::fail to upload recipe.\nrecipe id -> $recipeId.\nerror in -> $e.")
            }
        }
    }

    private fun createRecipeId(): String{
        val format = DateTimeFormatter.ofPattern("yyMMdd_HHmm")
        val createdAt = LocalDateTime.now().format(format)
        val recipeId = "${userId}_$createdAt"
        Log.d("LOG_CHECK", "createRecipeId()::recipe id is created. author id -> $userId. recipe id -> $recipeId")
        return recipeId
    }

    fun moveToNextStep(){
        val currentStep = _writeStep.value.ordinal
        if(currentStep >= RecipeWriteStep.values().last().ordinal) return
        _writeStep.value = RecipeWriteStep.values()[currentStep+1]
    }

    fun moveToPrevStep(){
        val currentStep = _writeStep.value.ordinal
        if(currentStep <= 0) return
        _writeStep.value = RecipeWriteStep.values()[currentStep-1]
    }
}