package com.dd2d.talkingrecipe2.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dd2d.talkingrecipe2.view.sub_screen.SubScreen

fun NavGraphBuilder.subScreenGraph(
    navController: NavController,
){
    composable(route = "${Screen.Sub.route}/{destination}"){ backStack->
        backStack.arguments?.getString("destination")?.let { destination->
            SubScreen(
                destination = destination
            )
        }
    }
}

/** [Screen.Sub]의 세부 종착점.
 * @property MyPost 작성글
 * @property SavePost 보관함. 저장한 레시피
 * @property Friend 친구 목록*/
enum class SubScreenDestination(val route: String, val description: String){
    MyPost("my post","작성글"), SavePost("save post", "보관함"), Friend("friend", "친구")
}