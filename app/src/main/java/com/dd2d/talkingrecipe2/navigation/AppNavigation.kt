package com.dd2d.talkingrecipe2.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
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
    var onLogin by remember { mutableStateOf(false) }



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
            requestLogout = { onLogin = false }
        )
    }
}