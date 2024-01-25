package com.dd2d.talkingrecipe2.data_struct.recipe

import android.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class StepInfo(
    val no: Long = System.currentTimeMillis(),
    val order: Int,
    val explanation: String = "",
    val imageUri: Uri = Uri.EMPTY,
){
    /** [StepInfo]의 내용물이 비었는지 확인.
     * @return [explanation] 또는 [imageUri] 중 하나라도 내용물이 비었다면 true 반납*/
    fun isEmpty() = this.explanation.isEmpty() || this.imageUri == Uri.EMPTY

    fun toDTO(recipeId: String, order: Int, imagePath: String) = StepInfoDTO(
        no = this.no,
        recipeId = recipeId,
        order = order,
        imagePath = imagePath,
        explanation = this.explanation,
    )
}


@Serializable
data class StepInfoDTO(
    @SerialName("no")
    val no: Long,
    @SerialName("recipe_id")
    val recipeId: String,
    @SerialName("image_path")
    val imagePath: String,
    @SerialName("order")
    val order: Int,
    @SerialName("explanation")
    val explanation: String = "",
){
    fun toStepInfo(imageUri: Uri) = StepInfo(
        no = this.no,
        order = this.order,
        explanation = this.explanation,
        imageUri = imageUri
    )
}
