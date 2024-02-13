package com.dd2d.talkingrecipe2.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dd2d.talkingrecipe2.alog
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view.login_screen.LoginScreen
import com.dd2d.talkingrecipe2.view.main_screen.MainScreen
import com.dd2d.talkingrecipe2.view.recipe_write_screen.CreateScreenValue
import com.dd2d.talkingrecipe2.view_model.LoginViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState
import com.dd2d.talkingrecipe2.view_model.UserViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
){
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel {
        UserViewModel(
            recipeFetchRepo = RecipeFetchRepositoryImpl(),
            recipeUploadRepo = RecipeUploadRepositoryImpl(context),
            userFetchRepo = UserFetchRepositoryImpl(),
            userUploadRepo = UserUploadRepositoryImpl()
        )
    }
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

//            TODO("아래에 있는 LaunchedEffect 테스트용임.")
            LaunchedEffect(key1 = Unit){
//                userViewModel.login(loginViewModel.fetchUserById("TalkingRecipe"))
                onLogin = true
            }


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
//                    onLogin = false
//                    userViewModel.logout()
                    val mode = RecipeViewModelMode.ReadMode
                    val recipeId = ""
                    navController.navigate(route = "test_recipe/${mode.name}")
                },
                onClickRecentRecipe = { recipeId-> navController.navigate(route = "${Screen.RecipeRead.route}/$recipeId") },
            )
        }
        subScreenGraph(navController = navController, userViewModel = userViewModel)
        recipeWriteScreenGraph(navController = navController)
        recipeSearchScreenGraph(navController = navController)
        recipeReadScreenGraph(
            navController = navController,
            onClickSavePost = { recipeId ->
//                userViewModel.s
            },
            updateUserRecentRecipe = { recipeId ->
                
            }
        )

        test(navController)
    }
}

fun NavGraphBuilder.test(navController: NavController){

    val userInfo = SimpleUserInfo.Empty

    composable(route = "test_recipe/{recipeViewModelMode}"){ backStack->
        val context = LocalContext.current
        val modeArgument = backStack.arguments?.getString("recipeViewModelMode")?: "Error"
        val recipeIdArgument = backStack.arguments?.getString("recipeId")?: ""

        val mode = RecipeViewModelMode.nameOf(modeArgument)
        val recipeViewModel = RecipeViewModel(
            recipeUploadRepo = RecipeUploadRepositoryImpl(context),
            recipeFetchRepo = RecipeFetchRepositoryImpl()
        )

        val recipeViewModelState by recipeViewModel.state.collectAsState()
        recipeViewModelState::class.simpleName?.alog("name")
        when(recipeViewModelState){
            is RecipeViewModelState.OnInit -> {
                "0".alog("0")
                recipeViewModel.init(
                    userInfo = userInfo,
                    recipeId = recipeIdArgument,
                    mode = mode
                )
            }
            is RecipeViewModelState.OnStable -> {
                "1".alog("1")
            }
            is RecipeViewModelState.OnConnected -> {
                "2".alog("2")
            }
            is RecipeViewModelState.OnError -> {
                "3".alog("3")
            }
        }
    }
}