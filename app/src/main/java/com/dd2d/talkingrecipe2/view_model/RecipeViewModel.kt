package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.model.RecipeFetchRepository
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


sealed class RecipeState{
    /** 뷰모델이 초기화된 상태. 뷰모델에서는 현 상태에서만 fetch함수를 사용할 수 있음. 또한 fetch함수가 실행되면 [OnLoading] 상태로 변하며 다시 현 상태로 돌아오지 않음.*/
    object Init: RecipeState(){
        init { Log.d("LOG_CHECK", "Recipe State : Init -> init view model.") }
    }
    /** 레시피를 다운 받는 상태. 이 상태에서는 [LoadingView]가 보여짐.*/
    class OnLoading(msg: String): RecipeState(){
        init { Log.d("LOG_CHECK", "Recipe State : OnLoading -> $msg") }
    }
    /** 레시피의 다운이 완료되어 레시피 보기가 가능한 상태.*/
    object Stable: RecipeState(){
        init { Log.d("LOG_CHECK", "Recipe State : Stable") }
    }
    /** 레시피의 정보를 다운 받는 중 에러가 발생한 상태. 이 상태에서는 [ErrorView]가 보여짐.*/
    class OnError(val cause: String): RecipeState(){
        init { Log.e("LOG_CHECK", "Recipe State : OnError -> $cause") }
    }
}

/** 앱 내에서 하나의 레시피를 다룰 때 사용함.
 * 뷰모델 내의 [RecipeState]의 값이 [RecipeState.Stable]일 경우에만 레시피의 정보를 가져올 수 있음.
 * @see [Screen.RecipeRead]
 * @see [Screen.RecipeWrite]*/
class RecipeViewModel(
    private val recipeRepo: RecipeFetchRepository,
    private val recipeId: String?,
): ViewModel() {
    private var _recipeState = MutableStateFlow<RecipeState>(RecipeState.Init)
    val recipeState = _recipeState.asStateFlow()

    private var _recipe = MutableStateFlow<Recipe>(Recipe())
    val recipe = _recipe.asStateFlow()

    /** 처음 초기화 됐을 경우에만 시행.
     *[recipeId] 값이 null인 경우 OnError, 아닌 경우 [recipeId]에 맞는 레시피의 정보를 가져옴. */
    fun init(){
        if(_recipeState.value is RecipeState.Init) {
            recipeId?.let {
                fetchRecipe(recipeId = recipeId)
            }?: run {
                _recipeState.value = RecipeState.OnError("init::recipe id is null value")
            }
        }
    }

    private fun fetchRecipe(recipeId: String){
        _recipeState.value = RecipeState.OnLoading("start fetching recipe. recipe id -> $recipeId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.IO){
                    _recipe.value = recipeRepo.fetRecipeById(
                        recipeId = recipeId,
                        onChangeFetchingState = { msg->
                            _recipeState.value = RecipeState.OnLoading(msg)
                        }
                    )

//                    TODO("테스트용임. 테스트 끝나면 위 주석 해제하고 하래 한 줄 지우면 됨.")
//                    val recipe = flowOf(TestingRecipe)

                    _recipeState.value = RecipeState.Stable
                }
            }
            catch (e: Exception){
                _recipeState.value = RecipeState.OnError("fetchRecipe()::fail to fetch recipe\nrecipe id -> $recipeId.\nmessage -> $e")
            }
        }
    }

}