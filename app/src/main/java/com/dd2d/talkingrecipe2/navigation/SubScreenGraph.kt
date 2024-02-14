package com.dd2d.talkingrecipe2.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination.Friend
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination.MyPost
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination.SavePost
import com.dd2d.talkingrecipe2.view.sub_screen.SubScreen
import com.dd2d.talkingrecipe2.view_model.RecipeViewModel
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode
import com.dd2d.talkingrecipe2.view_model.UserViewModel

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
    composable(route = "${Screen.Sub.route}/{destination}"){ backStack->
        val user by userViewModel.user.collectAsState()
        val friendList by userViewModel.friendList.collectAsState()
        val myPostList by userViewModel.myPostList.collectAsState()
        val savePostList by userViewModel.savePostList.collectAsState()

        @Suppress("MoveVariableDeclarationIntoWhen")
        val destinationRoute = backStack.arguments?.getString("destination")?: MyPost.route
        val selectedTab = when(destinationRoute){
            MyPost.route -> { MyPost }
            Friend.route -> { Friend }
            SavePost.route -> { SavePost }
            else -> { MyPost }
        }
        SubScreen(
            user = user,
            myPostList = myPostList,
            friendList = friendList,
            savePostList = savePostList,
            onUpdateUser = { update ->
                userViewModel.updateUser(update)
            },
            destination = selectedTab,
            onClickBack = { navController.navigateUp() },
            onClickPost = { recipePost ->
                val mode = RecipeViewModelMode.ReadMode.name
                navController.navigate(route = "${Screen.Recipe.route}/${mode}")
                recipeViewModel.fetchRecipeByPost(recipePost)
            },
            onClickFriend = {

            }
        )
    }
}