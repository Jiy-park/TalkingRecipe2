package com.dd2d.talkingrecipe2.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dd2d.talkingrecipe2.alog
import com.dd2d.talkingrecipe2.data_struct.LocalUser
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.logging
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingRecipeId
import com.dd2d.talkingrecipe2.ui.theme.kotex
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.login_screen.LoginScreen
import com.dd2d.talkingrecipe2.view.main_screen.MainScreen
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreen
import com.dd2d.talkingrecipe2.view_model.LoginViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode
import com.dd2d.talkingrecipe2.view_model.UserViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val userViewModel = viewModel {
        UserViewModel(
            recipeFetchRepo = RecipeFetchRepositoryImpl(),
            recipeUploadRepo = RecipeUploadRepositoryImpl(context),
            userFetchRepo = UserFetchRepositoryImpl(),
            userUploadRepo = UserUploadRepositoryImpl()
        )
    }
    val recipeViewModel = viewModel {
        RecipeViewModel(
            recipeUploadRepo = RecipeUploadRepositoryImpl(context),
            recipeFetchRepo = RecipeFetchRepositoryImpl()
        )
    }

    val navController = rememberNavController()
//    TODO("일단 아래 한 줄은 테스트용임. ")
    var onLogin by remember { mutableStateOf(true) }
    val loginUser by userViewModel.user.collectAsState()
    CompositionLocalProvider(
        LocalUser provides loginUser
    ) {
        NavHost(
            modifier = modifier.fillMaxSize(),
            navController = navController,
            startDestination = if (onLogin) Screen.Main.route else Screen.Login.route,
        ) {
            beforeLogin(
                navController = navController,
                onLogin = { loginUser ->
                    userViewModel.login(loginUser)
                    recipeViewModel.initUser(loginUser.toSimpleUserInfo())
                    onLogin = true
                }
            )

            afterLogin(
                navController = navController,
                userViewModel = userViewModel,
                recipeViewModel = recipeViewModel,
            )
        }
    }
}

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

fun NavGraphBuilder.afterLogin(
    navController: NavController,
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel
){
    composable(route = Screen.Main.route){
        MainScreen(
            onClickSearchTrigger = { navController.navigate(route = Screen.Search.route) },
            onClickSavePost = { navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.SavePost.route}") },
            onClickCreate = {
                val recipeViewModelMode = RecipeViewModelMode.WriteMode
                navController.navigate(route = "${Screen.Recipe.route}/${recipeViewModelMode.name}")
            },
            onClickMyPost = { navController.navigate(route = "${Screen.Sub.route}/${SubScreenDestination.MyPost.route}") },
            onClickSetting = {
                /*TODO("다이얼로그로 ")*/
//                    navController.navigate(route = "${Screen.RecipeRead.route}/$TestingRecipeId")
//                    onLogin = false
//                    userViewModel.logout()
                val mode = RecipeViewModelMode.ReadMode
                val recipeId = TestingRecipeId
                navController.navigate(route = "${Screen.Recipe.route}/${mode.name}")
                recipeViewModel.fetchRecipeById(recipeId)
            },
            onClickRecentRecipe = { recipeId-> navController.navigate(route = "${Screen.RecipeRead.route}/$recipeId") },
        )
    }
    subScreenGraph(navController = navController, userViewModel = userViewModel)
    recipeSearchScreenGraph(navController = navController)
    recipeScreenGraph(
        navController = navController,
        userViewModel = userViewModel,
        recipeViewModel = recipeViewModel
    )
//            recipeWriteScreenGraph(navController = navController)
//            recipeReadScreenGraph(
//                navController = navController,
//                onClickSavePost = { recipeId ->
//        //                userViewModel.s
//                },
//                updateUserRecentRecipe = { recipeId ->
//
//                }
//            )
}

fun NavGraphBuilder.recipeScreenGraph(
    navController: NavController,
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel
){
    composable(route = "${Screen.Recipe.route}/{recipeViewModelMode}"){ backStack->
        logging("sdf")
        val modeArgument = backStack.arguments?.getString("recipeViewModelMode")?: "Error"
        modeArgument.alog("mode")
        val recipeIdArgument = backStack.arguments?.getString("recipeId")?: ""
        recipeIdArgument.alog("id")

        var mode by remember { mutableStateOf(RecipeViewModelMode.nameOf(modeArgument)) }

        AnimatedContent(
            targetState = mode, label = "",
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
        ) { modeState->
            when(modeState){
                is RecipeViewModelMode.ReadMode -> {
                    RecipeReadScreen(
                        navController = navController,
                        userViewModel = userViewModel,
                        recipeViewModel = recipeViewModel
                    )
                }
                is RecipeViewModelMode.ModifyMode -> {
                    TempView(
                        label = "modify",
                        color = Color.Yellow,
                        onClick = { mode = it }
                    )
                }
                is RecipeViewModelMode.WriteMode -> {
                    TempView(
                        label = "write",
                        color = Color.LightGray,
                        onClick = { mode = it }
                    )
                }
                is RecipeViewModelMode.OnModeError -> {
                    ErrorView(
                        cause = "error in select mode",
                        onClickBack = { navController.navigateUp() }
                    )
                }
            }

        }
    }
}

@Composable
fun TempView(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    onClick: (RecipeViewModelMode) -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = color)
    ){
        kotex(text = label)
        TextButton(onClick = { onClick(RecipeViewModelMode.ReadMode) }) {
            kotex(text = "read")
        }
        TextButton(onClick = { onClick(RecipeViewModelMode.WriteMode) }) {
            kotex(text = "write")
        }
        TextButton(onClick = { onClick(RecipeViewModelMode.ModifyMode) }) {
            kotex(text = "modify")
        }
    }
}