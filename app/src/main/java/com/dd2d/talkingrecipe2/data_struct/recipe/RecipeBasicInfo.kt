package com.dd2d.talkingrecipe2.data_struct.recipe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 데이터베이스와의 통신을 위한 DTO*/
@Serializable
data class RecipeBasicInfoDTO(
    @SerialName("recipe_id")
    var recipeId: String,
    @SerialName("author_id")
    val authorId: String,
    @SerialName("title")
    var title: String,
    @SerialName("description")
    var description: String,
    @SerialName("level")
    var level: Int,
    @SerialName("time")
    var time: String,
    @SerialName("amount")
    var amount: String,
    @SerialName("calorie")
    var calorie: String,
    @SerialName("share_option")
    var shareOption: Int
){
    fun toRecipeBasicInfo() = RecipeBasicInfo(
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

data class RecipeBasicInfo(
    var recipeId: String = "",
    var authorId: String = "",
    var title: String = "",
    var description: String = "",
    var level: Level = Level.Normal,
    var time: String = "",
    var amount: String = "",
    var calorie: String = "",
    var shareOption: ShareOption = ShareOption.All
){
    fun toDTO() = RecipeBasicInfoDTO(
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
