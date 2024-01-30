package com.dd2d.talkingrecipe2.ui

import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.data_struct.AuthorInfo
import com.dd2d.talkingrecipe2.data_struct.Recipe
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
}

object TestingValue{
    const val TestingRecipeId = "TalkingRecipe_240115_0107"
    const val TestingUserId = "TalkingRecipe"
    const val TestingUserName = "toxi"
    val TestingUserProfileImageUri = R.drawable.main_screen_toxi.toUriWithDrawable()

    val TestingUser = User(
        userId = TestingUserId,
        name = TestingUserName,
        profileImageUri = R.drawable.complete_upload_recipe.toUriWithDrawable(),
        backgroundImageUri = R.drawable.temp.toUriWithDrawable(),
    )


    val TestingBasicInfo = RecipeBasicInfo(
        recipeId = TestingRecipeId,
        authorId = TestingUserId,
        title = "으앙",
        description = "설명??",
        level = Level.Easy,
        time = "140분",
        amount = "3인분",
        calorie = "111kcal",
        shareOption = ShareOption.All
    )
    val TestingIngredientList = mutableListOf(
        Ingredient(no = 0, name = "재료0", amount = "양0"),
        Ingredient(no = 1, name = "재료1", amount = "양1"),
        Ingredient(no = 2, name = "재료2", amount = "양2"),
        Ingredient(no = 3, name = "재료3", amount = "양3"),
        Ingredient(no = 4, name = "재료4", amount = "양4"),
        Ingredient(no = 5, name = "재료5", amount = "양5"),
        Ingredient(no = 5, name = "재료5", amount = "양5"),
        Ingredient(no = 5, name = "재료5", amount = "양5"),
        Ingredient(no = 5, name = "재료5", amount = "양5"),
        Ingredient(no = 5, name = "재료5", amount = "양5"),
        Ingredient(no = 5, name = "재료5", amount = "양5"),
    )

    val TestingStepInfoList = mutableListOf(
        StepInfo(no = 0L, order = 0, explanation = "설명0", imageUri = R.drawable.level_hard.toUriWithDrawable()),
        StepInfo(no = 1L, order = 1, explanation = "설명1", imageUri = R.drawable.main_screen_toxi.toUriWithDrawable()),
        StepInfo(no = 2L, order = 2, explanation = "설명2", imageUri = R.drawable.ic_setting.toUriWithDrawable()),
        StepInfo(no = 3L, order = 3, explanation = "설명3", imageUri = R.drawable.main_screen_search.toUriWithDrawable()),
        StepInfo(no = 4L, order = 4, explanation = "설명4", imageUri = R.drawable.temp.toUriWithDrawable()),
    )

    val TestingRecipe = Recipe(
        basicInfo = TestingBasicInfo,
        ingredientList = TestingIngredientList,
        stepInfoList = TestingStepInfoList,
        thumbnailUri = R.drawable.default_image_background.toUriWithDrawable()
    )

    val TestingAuthor = AuthorInfo(
        authorId = TestingUserId,
        name = TestingUserName,
        profileImageUri = TestingUserProfileImageUri
    )
}