package com.dd2d.talkingrecipe2.data_struct.recipe

import android.content.Context
import android.net.Uri
import com.dd2d.talkingrecipe2.alog
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class StepInfo(
    val no: Long = System.currentTimeMillis(),
    val order: Int,
    val explanation: String = "",
    val imageUri: Uri = Uri.EMPTY,
){
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

/** [RecipeWriteScreen]의 레시피 업로드 시 사용. [StepInfo]의 imageUri -> imagePath 로 변환. 이떄 imagePath는 order.tpye이 됨.
 * @param order 이미지의 순서
 * @param context 이미지의 mime type 추정에 사용*/
fun Uri.createStepInfoImagePath(order: Int, context: Context): String{
    val type = context.contentResolver.getType(this)?.let{ it.split("/")[1] }?: "jpeg"
    type.alog("step - $order")
    return "$order.$type"
}
