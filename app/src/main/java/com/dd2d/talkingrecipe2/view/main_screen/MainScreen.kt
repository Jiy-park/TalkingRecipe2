package com.dd2d.talkingrecipe2.view.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dd2d.talkingrecipe2.logging
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.ui.theme.BackgroundGradient
import com.dd2d.talkingrecipe2.view_model.UserViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel{
        UserViewModel(
            userFetchRepo = UserFetchRepositoryImpl(),
            userUploadRepo = UserUploadRepositoryImpl()
        )
    },
    onClickSearchTrigger: () -> Unit,
    onClickSavePost: ()->Unit,
    onClickCreate: ()->Unit,
    onClickMyPost: ()->Unit,
    onClickSetting: ()->Unit,
    onClickRecentRecipe: (recipeId: String)->Unit,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
            .background(brush = BackgroundGradient)
    ){
        MainScreenTopView()
        MainScreenCenterView(
            onClickSearchTrigger = { onClickSearchTrigger() }
        )
        MainScreenBottomView(
            onClickSavePost = { onClickSavePost() },
            onClickCreate = { onClickCreate() },
            onClickMyPost = { onClickMyPost() },
            onClickSetting = { onClickSetting() },
        )
//        RecentRecipe(
//            recipeBasicInfo = userViewModel.recentRecipeBasicInfo,
//            recipeAuthor = userViewModel.recentRecipeAuthor,
//            recipeThumbnail = userViewModel.recentRecipeThumbnail,
//            onClick = { onClickRecentRecipe(it) }
//        )
    }
}
