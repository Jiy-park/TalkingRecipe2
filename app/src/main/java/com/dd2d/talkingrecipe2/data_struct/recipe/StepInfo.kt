package com.dd2d.talkingrecipe2.data_struct.recipe

import android.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** @param version 레시피의 버전. 레시피의 버전에 맞는 [StepInfo]만 사용함.*/
data class StepInfo(
    val version: Int,
    val order: Int,
    val description: String,
    val imageUri: Uri,
){
    /** [StepInfo]의 내용물이 비었는지 확인.
     * @return [description] 또는 [imageUri] 중 하나라도 내용물이 비었다면 true 반납*/
    fun isEmpty() = this.description.isBlank() || this.imageUri == Uri.EMPTY

    fun toDTO(recipeId: String, order: Int, imagePath: String) = StepInfoDTO(
        version = this.version,
        recipeId = recipeId,
        order = order,
        imagePath = imagePath,
        description = this.description,
    )
}


@Serializable
data class StepInfoDTO(
    @SerialName("version")
    val version: Int,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("image_path")
    val imagePath: String,
    @SerialName("order")
    val order: Int,
    @SerialName("description")
    val description: String = "",
){
    fun toStepInfo(imageUri: Uri) = StepInfo(
        version = this.version,
        order = this.order,
        description = this.description,
        imageUri = imageUri
    )
}
