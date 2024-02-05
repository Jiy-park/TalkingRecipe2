package com.dd2d.talkingrecipe2.data_struct.recipe

import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.data_struct.recipe.Level.Unknown
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 데이터베이스와의 통신을 위한 DTO*/
@Serializable
data class RecipeBasicInfoDTO(
    @SerialName("version")
    val version: Int,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("author_id")
    val authorId: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("level")
    val level: Int,
    @SerialName("time")
    val time: String,
    @SerialName("amount")
    val amount: String,
    @SerialName("calorie")
    val calorie: String,
    @SerialName("share_option")
    val shareOption: Int
){
    fun toRecipeBasicInfo() = RecipeBasicInfo(
        version = this.version,
        recipeId = this.recipeId,
        authorId = this.authorId,
        title = this.title,
        description = this.description,
        level = Level.values()[this.level],
        time = this.time,
        amount = this.amount,
        calorie = this.calorie,
        shareOption = ShareOption.values()[this.shareOption]
    )
}

/** @param version 레시피의 버전.*/
data class RecipeBasicInfo(
    val version: Int = 0,
    val recipeId: String = "",
    val authorId: String = "",
    val title: String = "",
    val description: String = "",
    val level: Level = Level.Normal,
    val time: String = "",
    val amount: String = "",
    val calorie: String = "",
    val shareOption: ShareOption = ShareOption.All
){
    fun toDTO() = RecipeBasicInfoDTO(
        version = this.version,
        recipeId = this.recipeId,
        authorId = this.authorId,
        title = this.title,
        description = this.description,
        level = this.level.ordinal,
        time = this.time,
        amount = this.amount,
        calorie = this.calorie,
        shareOption = this.shareOption.ordinal,
    )
}


/** 레시피의 난이도
 * @property Unknown 난이도를 알 수 없음.*/
enum class Level(val description: String, val resId: Int){
    Unknown("-", R.drawable.complete_upload_recipe),
    Easy("쉬움", R.drawable.level_easy),
    Normal("보통", R.drawable.level_normal),
    Hard("어려움", R.drawable.level_hard)
}

enum class ShareOption(val description: String){
    NotShare("나만 공개"), Friends("친구 공개"), All("모두 공개")
}

