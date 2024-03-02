package com.dd2d.talkingrecipe2.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.alog
import com.dd2d.talkingrecipe2.data_struct.RecipePost
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination.Friend
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination.MyPost
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination.SavePost
import com.dd2d.talkingrecipe2.view.ErrorView
import com.dd2d.talkingrecipe2.view.LoadingView
import com.dd2d.talkingrecipe2.view.sub_screen.SubScreen
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode
import com.dd2d.talkingrecipe2.view_model.SubUiState
import com.dd2d.talkingrecipe2.view_model.SubViewModel
import com.dd2d.talkingrecipe2.view_model.UserViewModel
import kotlinx.coroutines.async

/** [Screen.Sub]의 세부 종착점.
 * @property MyPost 작성글
 * @property SavePost 보관함. 저장한 레시피
 * @property Friend 친구 목록*/
enum class SubScreenDestination(val route: String, val description: String){
    MyPost("my post","작성글"),
    Friend("friend", "친구"),
    SavePost("save post", "보관함"),
}

fun NavGraphBuilder.subScreenGraph(
    navController: NavController,
    userViewModel: UserViewModel,
    recipeViewModel: RecipeViewModel
){
    composable(route = "${Screen.Sub.route}/{destination}/{userId}"){ backStack->
        val context = LocalContext.current
        val subViewModel = viewModel {
            SubViewModel(
                userFetchRepo = UserFetchRepositoryImpl(),
                userUploadRepo = UserUploadRepositoryImpl(),
                recipeFetchRepo = RecipeFetchRepositoryImpl(),
                recipeUploadRepo = RecipeUploadRepositoryImpl(context)
            )
        }

        val destination = backStack.arguments?.getString("destination")?.let { name->
            when(name){
                MyPost.route -> { MyPost }
                SavePost.route -> { SavePost }
                Friend.route -> { Friend }
                else -> { MyPost }
            }
        }?: MyPost
        destination.alog("des")
        val userId = backStack.arguments?.getString("userId")?: run {
            subViewModel.requestState(SubUiState.OnError("user id is unexpected value."))
            User.ErrorInId
        }

        val uiState by subViewModel.uiState.collectAsState()
        var myPostList by remember { mutableStateOf<List<RecipePost>>(emptyList()) }
        var friendList by remember { mutableStateOf<List<SimpleUserInfo>>(emptyList()) }
        var savePostList by remember { mutableStateOf<List<RecipePost>>(emptyList()) }
        val loginUser by userViewModel.user.collectAsState()
        var user by remember{
            mutableStateOf(
                loginUser
//                if(userId == loginUser.userId) { loginUser }
//                else { subViewModel.fetchUserInfoById(userId) }
            )
        }

        LaunchedEffect(key1 = true){
            if(userId != User.ErrorInId){
                val myPostListFetcher = async { subViewModel.fetchMyPostListByUserId(userId) }
                val saveListFetcher = async { subViewModel.fetchSavePostListByUserId(userId) }
                val friendListFetcher = async { subViewModel.fetchFriendListByUserId(userId) }

                if(userId != loginUser.userId) {
                    val userFetcher = async { subViewModel.fetchUserInfoById(userId) }
                    user = userFetcher.await()
                }
                myPostList = myPostListFetcher.await()
                savePostList = saveListFetcher.await()
                friendList = friendListFetcher.await()

            }
        }

        when(uiState){
            is SubUiState.OnFetching -> { LoadingView() }
            is SubUiState.OnError -> {
                ErrorView(
                    cause = (uiState as SubUiState.OnError).msg,
                    onClickBack = { navController.navigateUp() }
                )
            }
            is SubUiState.OnStable -> {
                SubScreen(
                    user = user,
                    myPostList = myPostList,
                    friendList = friendList,
                    savePostList = savePostList,
                    onUpdateUser = { update -> userViewModel.updateUser(update) },
                    destination = destination,
                    onClickPost = { post->
                        val mode = RecipeViewModelMode.ReadMode.name
                        navController.navigate(route = "${Screen.Recipe.route}/$mode")
                        recipeViewModel.fetchRecipeByPost(post)
                    },
                    onClickFriend = { simpleUserInfo ->
                        navController.navigate(route = "${Screen.Sub.route}/${MyPost.route}/${simpleUserInfo.userId}"){
                            popUpTo(route = "${Screen.Sub.route}/${MyPost.route}/${userId}"){
                                inclusive = true
                                saveState = true
                            }
                        }
                    },
                    onClickBack = { navController.navigateUp() }
                )
            }
        }
    }
}

