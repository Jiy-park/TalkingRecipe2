package com.dd2d.talkingrecipe2.data_struct.recipe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Ingredient(
    val no: Int? = null,
    val name: String = "",
    val amount: String = "",
){
    fun toDTO(recipeId: String) = IngredientDTO(
        no = this.no,
        recipeId = recipeId,
        name = this.name,
        amount = this.amount
    )
}

@Serializable
data class IngredientDTO(
    @SerialName("no")
    val no: Int?,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("name")
    val name: String,
    @SerialName("amount")
    val amount: String,
){
    fun toIngredient() = Ingredient(
        no = this.no,
        name = this.name,
        amount = this.amount
    )
}

fun List<Ingredient>.toDTO(recipeId: String) = this.map { it.toDTO(recipeId) }