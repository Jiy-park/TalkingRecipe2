package com.dd2d.talkingrecipe2.data_struct

import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.view_model.ShareOption
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/** 뷰에 띄어질 최종적인 레시피 정보
 * @param thumbnailPath 임시로 이미지 넣어둠.
 * @param recipeId 1_000_001부터 시작.*/
@Serializable
data class Recipe(
    val recipeId: String = "",
    val author: String = "",
    val title: String = "레시피 제목",
    val description: String = "레시피 설명",
//    val thumbnailPath: String = "", 나중에 사용
    val thumbnailPath: Int = R.drawable.default_image,
    val shareOption: ShareOption = ShareOption.All,
    val level: Level = Level.Unknown,
    val calorie: Int = -1,
    val amount: Int = -1,
    val time: Int = -1,
)

@Serializable
data class RecipeDTO(
    @SerialName("no")
    val no: Int,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("author_id")
    val authorId: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("time")
    val time: Int,
    @SerialName("amount")
    val amount: Int,
    @SerialName("level")
    val level: Int,
    @SerialName("share_option")
    val shareOption: Int,
    @SerialName("calorie")
    val calorie: Int,
)

//
//val sampleRecipe = Recipe(
//    recipeId = 1_000_001,
//    author = sampleUser.nickname,
//    title = "sample recipe",
//    thumbnailPath = R.drawable.default_image,
//    description = "테스트용 레시피",
//    shareOption = ShareOption.All,
//    level = Level.Hard,
//    calorie = 999,
//    amount = 3,
//    time = 40
//)
//val sampleRecipeIngredientList = listOf(
//    Ingredient("재료1","분량1"),
//    Ingredient("재료2","분량2"),
//    Ingredient("재료3","분량3"),
//    Ingredient("재료4","분량4"),
//    Ingredient("재료5","분량5"),
//    Ingredient("재료6","분량6"),
//)
//
//val sampleRecipeStepInfoList = listOf(
//    StepInfo(Uri.EMPTY, "설명1"),
//    StepInfo(Uri.EMPTY, "설명2"),
//    StepInfo(Uri.EMPTY, "설명3"),
//    StepInfo(Uri.EMPTY, "설명4"),
//    StepInfo(Uri.EMPTY, "설명5"),
//    StepInfo(Uri.EMPTY, "설명6"),
//)

/** 레시피의 난이도
 * @property Unknown 난이도를 알 수 없음.*/
enum class Level(val description: String, val resId: Int){
    Unknown("-", R.drawable.complete_upload_recipe),
    Easy("쉬움", R.drawable.level_easy),
    Normal("보통", R.drawable.level_normal),
    Hard("어려움", R.drawable.level_hard)
}