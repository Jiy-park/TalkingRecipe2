package com.dd2d.talkingrecipe2.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view.login_screen.LoginScreen
import com.dd2d.talkingrecipe2.view.main_screen.MainScreen
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreen
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreen
import com.dd2d.talkingrecipe2.view.sub_screen.SubScreen
import com.dd2d.talkingrecipe2.view_model.LoginViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
import com.dd2d.talkingrecipe2.view_model.UserViewModel

/** 로그인 전의 화면을 관리.
 * * [LoginScreen]*/
fun NavGraphBuilder.beforeLogin(
    navController: NavController,
    onLogin: (loginUser: User)->Unit,
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
                    val loginUser = loginViewModel.fetchUserById(userId)
                    onLogin(loginUser)
                    true
                }
                else{
                    false
                }
            },
            joinNewUser = { userId, userPassword, userName ->
                loginViewModel.joinNewUser(userId, userPassword, userName) { user->
                    onLogin(user)
                }
            }
        )
    }
}

/** 로그인 후의 화면을 관리.
 * * [MainScreen]
 * * [SubScreen]
 * * [RecipeReadScreen]
 * * [RecipeWriteScreen]
 * */
fun NavGraphBuilder.afterLogin(
    navController: NavController,
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel,
    requestLogout: ()->Unit,
){
    mainGraph(
        navController = navController,
        userViewModel = userViewModel,
        recipeViewModel = recipeViewModel,
        requestLogout = { requestLogout() }
    )
    subScreenGraph(
        navController = navController,
        userViewModel = userViewModel,
        recipeViewModel = recipeViewModel
    )
    searchScreenGraph(
        navController = navController
    )
    recipeScreenGraph(
        navController = navController,
        userViewModel = userViewModel,
        recipeViewModel = recipeViewModel
    )
}