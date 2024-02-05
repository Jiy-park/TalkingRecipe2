package com.dd2d.talkingrecipe2.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dd2d.talkingrecipe2.logging
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view.login_screen.LoginScreen
import com.dd2d.talkingrecipe2.view.main_screen.MainScreen
import com.dd2d.talkingrecipe2.view.recipe_write_screen.CreateScreenValue
import com.dd2d.talkingrecipe2.view_model.UserState
import com.dd2d.talkingrecipe2.view_model.UserViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel {
        UserViewModel(
            userFetchRepo = UserFetchRepositoryImpl(),
            userUploadRepo = UserUploadRepositoryImpl()
        )
    }
){
    val navController = rememberNavController()
    val userState by userViewModel.userState.collectAsState()

//    TODO("아래에서 두번째에 있는 startDestination -> 테스트용임. 첫번째 주석 해제. 두번째 지우기.")
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = if(userState is UserState.OnLogin) Screen.Main.route else Screen.Login.route,
//        startDestination = Screen.Main.route,
    ){
        composable(route = Screen.Login.route){
            LoginScreen(
                checkDuplicateUserId = { userId ->
                    logging("userId :$userId")
                    userViewModel.checkDuplicateUserId(userId)
                },
                tryLogin = { userId, userPassword ->
                    userViewModel.tryLogin(userId, userPassword)
                },
                joinNewUser = { userId, userPassword, userName ->
                    userViewModel.joinNewUserWithLogin(userId, userPassword, userName)
                }
            )
        }

        composable(route = Screen.Main.route){
            MainScreen(
                onClickSearchTrigger = { navController.navigate(route = Screen.RecipeSearch.route) },
                onClickSavePost = { navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.SavePost.route}") },
                onClickCreate = { navController.navigate(route = "${Screen.RecipeWrite.route}/${CreateScreenValue.CreateMode}") },
                onClickMyPost = { navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.MyPost.route}") },
                onClickSetting = {
                     /*TODO("다이얼로그로 ")*/
//                    navController.navigate(route = "${Screen.RecipeRead.route}/$TestingRecipeId")
                                 userViewModel.logout()
                },
                onClickRecentRecipe = { recipeId-> navController.navigate(route = "${Screen.RecipeRead.route}/$recipeId") },
            )
        }
        subScreenGraph(navController = navController, userViewModel = userViewModel)
        recipeWriteScreenGraph(navController = navController)
        recipeSearchScreenGraph(navController = navController)
        recipeReadScreenGraph(navController = navController)
    }
}