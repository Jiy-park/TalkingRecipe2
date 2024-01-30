package com.dd2d.talkingrecipe2.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.data_struct.recipe_write.RecipeWriteMode
import com.dd2d.talkingrecipe2.model.RecipeFetchRepository
import com.dd2d.talkingrecipe2.model.RecipeUploadRepository
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingUserId
import com.dd2d.talkingrecipe2.view.recipe_write_screen.CreateScreenValue
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreen
import com.dd2d.talkingrecipe2.view_model.RecipeWriteViewModel

fun NavGraphBuilder.recipeWriteScreenGraph(
    navController: NavController
){
    composable(route = "${Screen.RecipeWrite.route}/{recipeId}"){ backStack->
        /** 레시피 수정 모드. 수정 모드일 경우 [RecipeWriteScreen]에 매개변수로 수정할 레시피의 아이디값을 넘겨줌. 아닐 경우 [CreateScreenValue.CreateMode]을 받음.*/
//        val mode = backStack.arguments?.getString("recipeId")?.let { recipeId->
//            if(recipeId == CreateScreenValue.CreateMode) CreateScreenMode.Create
//            else CreateScreenMode.Modify(recipeId)
//        }?: CreateScreenMode.Create

//        TODO("아래 한 줄은 테스트용. 위의 주석을 해제 후 아래 한 줄 지우면 됨.")
        val mode = RecipeWriteMode.Modify("TalkingRecipe_240124_1526")

        val context = LocalContext.current
        val recipeFetchRepo = RecipeFetchRepository()
        val recipeUploadRepo = RecipeUploadRepository(context)
        val writeViewModel: RecipeWriteViewModel = viewModel{
            RecipeWriteViewModel(userId = TestingUserId, writeScreenMode = mode, recipeFetchRepo = recipeFetchRepo, recipeUploadRepo = recipeUploadRepo)
        }

        RecipeWriteScreen(
            writeViewModel = writeViewModel,
            onClickBack = { navController.navigateUp() },
            onClickMoveToMain = { navController.navigateUp() },
            onClickMoveToRecipe = { recipeId->
                navController.navigate(route = "${Screen.RecipeRead.route}/$recipeId")
            }
        )
    }
}