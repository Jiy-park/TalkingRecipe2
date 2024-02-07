package com.dd2d.talkingrecipe2.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dd2d.talkingrecipe2.alog
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view.login_screen.LoginScreen
import com.dd2d.talkingrecipe2.view.main_screen.MainScreen
import com.dd2d.talkingrecipe2.view.recipe_write_screen.CreateScreenValue
import com.dd2d.talkingrecipe2.view_model.LoginViewModel
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
//    TODO("일단 아래 한 줄은 테스트용임. ")
    var onLogin by remember { mutableStateOf(false) }

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = if(onLogin) Screen.Main.route else Screen.Login.route,
    ){
        composable(route = Screen.Login.route){
            val loginViewModel = viewModel{
                LoginViewModel(
                    userUploadRepo = UserUploadRepositoryImpl(),
                    userFetchRepo = UserFetchRepositoryImpl()
                )
            }
            val loginState by loginViewModel.loginState.collectAsState()
            LoginScreen(
                loginState = loginState,
                checkDuplicateUserId = { userId ->
                    loginViewModel.checkDuplicateUserId(userId)
                },
                tryLogin = { userId, userPassword ->
                    if(loginViewModel.tryLogin(userId, userPassword)) {
                        userViewModel.login(loginViewModel.fetchUserById(userId))
                        onLogin = true
                        true
                    }
                    else{
                        false
                    }
                },
                joinNewUser = { userId, userPassword, userName ->
                    loginViewModel.joinNewUser(userId, userPassword, userName) { user->
                        user.alog("login user ->")
                    }
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
                    onLogin = false
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