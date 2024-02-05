package com.dd2d.talkingrecipe2.data_struct.recipe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** @param version 레시피의 버전. 레시피의 버전에 맞는 [Ingredient]만 사용함.*/
data class Ingredient(
    val version: Int,
    val no: Int,
    val name: String = "",
    val amount: String = "",
){
    /** [Ingredient]의 내용물이 비었는지 확인.
     * @return [name] 또는 [amount]이 비었을 경우 true 반납.*/
    fun isEmpty() = this.name.isBlank() || this.amount.isBlank()

    fun toDTO(recipeId: String) = IngredientDTO(
        version = this.version,
        no = this.no,
        recipeId = recipeId,
        name = this.name,
        amount = this.amount
    )
}

@Serializable
data class IngredientDTO(
    @SerialName("version")
    val version: Int,
    @SerialName("no")
    val no: Int,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("name")
    val name: String,
    @SerialName("amount")
    val amount: String,
){
    fun toIngredient() = Ingredient(
        version = this.version,
        no = this.no,
        name = this.name,
        amount = this.amount
    )
}

fun List<Ingredient>.toDTO(recipeId: String) = this.map { it.toDTO(recipeId) }