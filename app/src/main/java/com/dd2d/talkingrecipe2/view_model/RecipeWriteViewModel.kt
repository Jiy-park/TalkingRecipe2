package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.recipe_create.CreateStep
import com.dd2d.talkingrecipe2.data_struct.recipe_create.WriteStep
import com.dd2d.talkingrecipe2.model.RecipeFetchRepository
import com.dd2d.talkingrecipe2.model.RecipeUploadRepository
import com.dd2d.talkingrecipe2.navigation.CreateScreenMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


sealed class WriteState{
    object Init: WriteState(){
        init { Log.d("LOG_CHECK", "Write State : Init -> initial view model") }
    }
    class OnFetching(msg: String): WriteState(){
        init { Log.d("LOG_CHECK", "Write State : OnFetching -> $msg") }
    }
    class OnUploading(msg: String): WriteState(){
        init { Log.d("LOG_CHECK", "Create State : OnUploading -> $msg") }
    }
    object Stable: WriteState(){
        init { Log.d("LOG_CHECK", "Recipe State : Stable") }
    }
    object OnEndUploading: WriteState(){
        init { Log.d("LOG_CHECK", "Recipe State : OnEndUploading -> upload recipe is finished.") }
    }
    class OnError(val cause: String): WriteState(){
        init { Log.e("LOG_CHECK", "Write State : OnError -> $cause") }
    }
}

class RecipeWriteViewModel(
    private val userId: String,
    val writeScreenMode: CreateScreenMode,
    private val recipeUploadRepo: RecipeUploadRepository,
    private val recipeFetchRepo: RecipeFetchRepository,
): ViewModel() {
    private var _writeState = MutableStateFlow<WriteState>(WriteState.Init)
    val writeState: StateFlow<WriteState> = _writeState.asStateFlow()

    private var _writeStep = MutableStateFlow<WriteStep>(WriteStep.RecipeBasicInfo)
    val writeStep: StateFlow<WriteStep> = _writeStep.asStateFlow()

    private var _recipe = MutableStateFlow(Recipe())
    val recipe: StateFlow<Recipe> = _recipe.asStateFlow()

    fun onChangeRecipe(recipe: Recipe){
        _recipe.value = recipe
    }

    fun init(){
        if(_writeState.value == WriteState.Init){
            when(writeScreenMode){
                is CreateScreenMode.Create -> {
                    _recipe.value = Recipe()
                    _writeState.value = WriteState.Stable
                }
                is CreateScreenMode.Modify -> {
                    val recipeId = writeScreenMode.recipeId
                    fetchRecipe(recipeId)
                }
            }

        }
    }

    /** 레시피 작성이 완료되어 업로드를 시작함.
     * [_writeState]의 값이 [WriteState.Stable]이면서 [_writeStep]의 값이 [WriteStep.RecipeThumbnail]일 때만 작동해야 함.*/
    fun endWrite(){
        if(_writeState.value == WriteState.Stable && _writeStep.value == WriteStep.RecipeThumbnail){
            uploadRecipe()
        }
    }

    /** 데이터베이스에서 [recipeId]에 맞는 레시피를 가져옴.
     * 가져오는 동안 [WriteState.OnFetching] 상태이며 가져온 레시피는 [Flow]형태임.
     * [writeState]의 값이 [WriteState.Stable]일 때 레시피에 접근 가능.*/
    private fun fetchRecipe(recipeId: String){
        _writeState.value = WriteState.OnFetching("start fetch recipe. recipe id -> $recipeId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _recipe.value = recipeFetchRepo
                    .fetRecipeById(
                        recipeId = recipeId,
                        onChangeFetchingState = { msg->
                            _writeState.value = WriteState.OnFetching(msg)
                        }
                    )

                _writeState.value = WriteState.Stable
            }
            catch (e: Exception){
                _writeState.value = WriteState.OnError("fetchRecipe()::fail to fetch recipe.\nrecipe id -> $recipeId.\nerror in -> $e.")
            }
        }
    }

    private fun uploadRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            _writeState.value = WriteState.OnUploading("start upload recipe.")
            if(writeScreenMode is CreateScreenMode.Create){
                val recipeId = createRecipeId()
                val updateBasicInfo = _recipe.value.basicInfo.copy(recipeId = recipeId)
                _recipe.value = _recipe.value.copy(basicInfo = updateBasicInfo)
            }

            val recipeId = _recipe.value.basicInfo.recipeId

            try {
                recipeUploadRepo.uploadRecipe(
                    recipe = _recipe.value,
                    onChangeUploadState = { msg ->
                        _writeState.value = WriteState.OnUploading(msg)
                    },
                    onEndUpload = {
                        _writeState.value = WriteState.OnEndUploading
                    }
                )
            }
            catch (e: Exception){
                _writeState.value = WriteState.OnError("uploadRecipe()::fail to upload recipe.\nrecipe id -> $recipeId.\nerror in -> $e.")
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
        val currentStep = _writeStep.value.step
        if(currentStep >= CreateStep.values().last().step) return
        _writeStep.value = WriteStep.values()[currentStep+1]
    }

    fun moveToPrevStep(){
        val currentStep = _writeStep.value.step
        if(currentStep <= 0) return
        _writeStep.value = WriteStep.values()[currentStep-1]
    }
}