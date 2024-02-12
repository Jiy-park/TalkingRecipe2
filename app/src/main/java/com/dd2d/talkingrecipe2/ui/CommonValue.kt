package com.dd2d.talkingrecipe2.ui

import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.data_struct.Recipe
import com.dd2d.talkingrecipe2.data_struct.RecipePost
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.Level
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.ShareOption
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.toUriWithDrawable

object CommonValue {
    /** 앱 하단 버튼의 높이. 50.dp*/
    val BottomButtonHeight = 50.dp
    /** 앱 상단 영역의 높이. 60.dp*/
    val TopViewHeight = 60.dp

    /** 레시피 보기의 제작자 정보 영역의 높이. 120.dp*/
    val AuthorInfoHeight = 120.dp

    /** 레시피 보기의 토킹레시피 부분에서 보이는 작성자 정보 영역의 높이*/
    val SimpleAuthorInfoHeight = 60.dp

    /** 레시피 보기에서 보이는 레시피의 썸네일 높이. 250.dp*/
    val RecipeScreenRecipeThumbnailImageHeight = 250.dp

    /** 레시피 보기의 토킹레시피 부분에서 각 단계의 이미지의 높이*/
    val TalkingRecipeStepImageHeight = 200.dp

    /** 레시피 보기에서 각 재료가 보이는 뷰의 높이. 30.dp*/
    val IngredientViewHeight = 30.dp

    /** 시피 보기에서 각 재료가 보이는 뷰의 세로 간격. 10.dp*/
    val IngredientViewVerticalPaddingValue = 10.dp

    /** 레시피의 각 단계에서 보여주는 이미지의 높이. 140.dp*/
    val StepInfoViewHeight = 140.dp

    /** 레시피의 썸네일 이미지 비율. 1.6 : 1.0*/
    const val RecipeThumbnailImageRatio = 1.64F/1.0F

    /** 친구 목록에서 보여지는 프로필이미지의 높이*/
    val FriendViewerHeight = 50.dp
}

object TestingValue{
    const val TestingRecipeId = "TalkingRecipe_240115_0107"
    const val TestingUserId = "TalkingRecipe"
    const val TestingUserName = "toxi"
    val TestingUserProfileImageUri = R.drawable.main_screen_toxi.toUriWithDrawable()

    val TestingUser = User(
        createdAt = 1,
        userId = TestingUserId,
        name = TestingUserName,
        recentRecipeId = TestingRecipeId,
        profileImageUri = R.drawable.complete_upload_recipe.toUriWithDrawable(),
        backgroundImageUri = R.drawable.temp.toUriWithDrawable(),
    )
    val TestingAuthor = SimpleUserInfo(
        userId = TestingUserId,
        userName = TestingUserName,
        userProfileImageUri = TestingUserProfileImageUri
    )

    val TestingBasicInfo = RecipeBasicInfo(
        version = 0,
        recipeId = TestingRecipeId,
        authorId = TestingUserId,
        title = "으앙",
        description = "설명??",
        level = Level.Easy,
        time = "140",
        amount = "3",
        calorie = "111",
        shareOption = ShareOption.All
    )
    val TestingIngredientList = mutableListOf(
        Ingredient(version = 1, name = "재료1", amount = "양1", no = 1),
        Ingredient(version = 2, name = "재료2", amount = "양2", no = 2),
        Ingredient(version = 3, name = "재료3", amount = "양3", no = 3),
        Ingredient(version = 4, name = "재료4", amount = "양4", no = 4),
        Ingredient(version = 5, name = "재료5", amount = "양5", no = 5),
        Ingredient(version = 5, name = "재료5", amount = "양5", no = 5),
        Ingredient(version = 5, name = "재료5", amount = "양5", no = 5),
        Ingredient(version = 5, name = "재료5", amount = "양5", no = 5),
        Ingredient(version = 5, name = "재료5", amount = "양5", no = 5),
        Ingredient(version = 5, name = "재료5", amount = "양5", no = 5),
    )

    val TestingStepInfoList = mutableListOf(
        StepInfo(version = 0, order = 0, description = "설명0", imageUri = R.drawable.level_hard.toUriWithDrawable()),
        StepInfo(version = 1, order = 1, description = "설명1", imageUri = R.drawable.main_screen_toxi.toUriWithDrawable()),
        StepInfo(version = 2, order = 2, description = "설명2", imageUri = R.drawable.ic_setting.toUriWithDrawable()),
        StepInfo(version = 3, order = 3, description = "설명3", imageUri = R.drawable.main_screen_search.toUriWithDrawable()),
        StepInfo(version = 4, order = 4, description = "설명4", imageUri = R.drawable.temp.toUriWithDrawable()),
    )

    val TestingRecipe = Recipe(
        basicInfo = TestingBasicInfo,
        ingredientList = TestingIngredientList,
        stepInfoList = TestingStepInfoList,
        thumbnailUri = R.drawable.default_image_background.toUriWithDrawable()
    )

    val TestingPostList = listOf<RecipePost>(
        RecipePost(
            recipeBasicInfo = TestingBasicInfo.copy(recipeId = "1"),
            thumbnailImageUri = R.drawable.default_image_background.toUriWithDrawable(),
            author = "$TestingUserName @$TestingUserId"
        ),
        RecipePost(
            recipeBasicInfo = TestingBasicInfo.copy(recipeId = "2"),
            thumbnailImageUri = R.drawable.temp.toUriWithDrawable(),
            author = "$TestingUserName @$TestingUserId"
        ),
        RecipePost(
            recipeBasicInfo = TestingBasicInfo.copy(recipeId = "3"),
            thumbnailImageUri = R.drawable.temp.toUriWithDrawable(),
            author = "$TestingUserName @$TestingUserId"
        ),
        RecipePost(
            recipeBasicInfo = TestingBasicInfo.copy(recipeId = "4"),
            thumbnailImageUri = R.drawable.temp.toUriWithDrawable(),
            author = "$TestingUserName @$TestingUserId"
        ),
        RecipePost(
            recipeBasicInfo = TestingBasicInfo.copy(recipeId = "5"),
            thumbnailImageUri = R.drawable.temp.toUriWithDrawable(),
            author = "$TestingUserName @$TestingUserId"
        ),
    )

    val TestingFriendList = listOf(
        SimpleUserInfo("1asdasd", "1eqweqe", R.drawable.level_hard.toUriWithDrawable()),
        SimpleUserInfo("2vvvv", "2vfdfv", R.drawable.level_easy.toUriWithDrawable()),
        SimpleUserInfo("3ddd", "3sfsdv", R.drawable.default_image_background.toUriWithDrawable()),
        SimpleUserInfo("4vbfg", "4bgs", R.drawable.level_normal.toUriWithDrawable()),
        SimpleUserInfo("5123", "5333", R.drawable.main_screen_toxi.toUriWithDrawable()),
    )
}