package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepository
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


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

/** 레시피를 읽을 때 사용.
 * 뷰모델 내의 [RecipeState]의 값이 [RecipeState.Stable]일 경우에만 레시피의 정보를 가져올 수 있음.
 * @see [Screen.RecipeRead]
 * @see [Screen.RecipeWrite]*/
class RecipeReadViewModel(
    private val recipeRepo: RecipeFetchRepositoryImpl,
    private val recipeId: String?,
): ViewModel() {
    private var _recipeState = MutableStateFlow<RecipeState>(RecipeState.Init)
    val recipeState = _recipeState.asStateFlow()

    private var _recipe = MutableStateFlow<Recipe>(Recipe.EmptyRecipe)
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

    /** [recipeId]에 맞는 레시피를 [RecipeFetchRepository]로부터 [Recipe]형태로 받아온다.
     * @see RecipeFetchRepository*/
    fun fetchRecipe(recipeId: String){
        _recipeState.value = RecipeState.OnLoading("fetchRecipe()::start fetching recipe. recipe id -> $recipeId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                with(recipeRepo) {
                    val basicInfo = async {
                        _recipeState.value = RecipeState.OnLoading("fetchRecipe()::start fetching recipe basic info")
                        fetchRecipeBasicInfoById(recipeId)
                    }

                    val ingredientList = async {
                        _recipeState.value = RecipeState.OnLoading("fetchRecipe()::start fetching recipe ingredient list")
                        fetchRecipeIngredientListById(recipeId, 1L)
                    }

                    val stepInfoList = async {
                        _recipeState.value = RecipeState.OnLoading("fetchRecipe()::start fetching recipe step info list")
                        fetchRecipeStepInfoListById(recipeId, 1L)
                    }

                    val thumbnailUri = async {
                        _recipeState.value = RecipeState.OnLoading("fetchRecipe()::start fetching recipe thumbnail image uri")
                        fetchRecipeThumbnailUriById(recipeId)
                    }

                    val authorInfo = async {
                        _recipeState.value = RecipeState.OnLoading("fetchRecipe()::start fetching recipe author info")
                        fetchRecipeAuthorInfo(recipeId)
                    }

                    _recipe.value =Recipe(
                        basicInfo = basicInfo.await(),
                        ingredientList = ingredientList.await(),
                        stepInfoList = stepInfoList.await(),
                        thumbnailUri = thumbnailUri.await(),
                        authorInfo = authorInfo.await()
                    )

                }
                _recipeState.value = RecipeState.Stable
            }
            catch (e: Exception){
                _recipeState.value = RecipeState.OnError("fetchRecipe()::fail to fetch recipe\nrecipe id -> $recipeId.\nmessage -> $e")
            }
        }
    }

}