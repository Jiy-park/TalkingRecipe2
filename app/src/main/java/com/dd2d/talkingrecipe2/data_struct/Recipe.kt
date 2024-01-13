package com.dd2d.talkingrecipe2.data_struct

import com.dd2d.talkingrecipe2.R
import kotlinx.serialization.Serializable


/** 뷰에 띄어질 최종적인 레시피 정보
 * @param thumbnailPath 임시로 이미지 넣어둠.
 * @param recipeId 1_000_001부터 시작.*/
@Serializable
data class Recipe(
    val recipeId: Int = -1,
    val author: String = "",
    val title: String = "레시피 제목",
//    val thumbnailPath: String = "", 나중에 사용
    val thumbnailPath: Int = R.drawable.default_image,
    val description: String = "레시피 설명",
    val level: Level = Level.Unknown,
    val calorie: Int = -1,
    val amount: Int = -1,
    val time: Int = -1,
){

}

val sampleRecipe = Recipe(
    recipeId = 1_000_001,
    author = sampleUser.nickname,
    title = "sample recipe",
    thumbnailPath = R.drawable.default_image,
    description = "테스트용 레시피",
    level = Level.Hard,
    calorie = 999,
    amount = 3,
    time = 40
)


/** 레시피의 난이도
 * @property Unknown 난이도를 알 수 없음.*/
enum class Level(val description: String){
    Unknown("-"), Easy("쉬움"), Normal("보통"), Hard("어려움")
}