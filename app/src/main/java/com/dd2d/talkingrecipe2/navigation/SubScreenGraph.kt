package com.dd2d.talkingrecipe2.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination.Friend
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination.MyPost
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination.SavePost
import com.dd2d.talkingrecipe2.view.sub_screen.SubScreen

fun NavGraphBuilder.subScreenGraph(
    navController: NavController,
){
    composable(route = "${Screen.Sub.route}/{destination}"){ backStack->
        val destinationRoute = backStack.arguments?.getString("destination")?: SubScreenDestination.MyPost.route
        val selectedTab = when(destinationRoute){
            SubScreenDestination.MyPost.route -> { SubScreenDestination.MyPost }
            SubScreenDestination.Friend.route -> { SubScreenDestination.Friend }
            SubScreenDestination.SavePost.route -> { SubScreenDestination.SavePost }
            else -> { SubScreenDestination.MyPost }
        }
        SubScreen(
            destination = selectedTab
        )
    }
}

/** [Screen.Sub]의 세부 종착점.
 * @property MyPost 작성글
 * @property SavePost 보관함. 저장한 레시피
 * @property Friend 친구 목록*/
enum class SubScreenDestination(val route: String, val description: String){
    MyPost("my post","작성글"),
    Friend("friend", "친구"),
    SavePost("save post", "보관함"),
}