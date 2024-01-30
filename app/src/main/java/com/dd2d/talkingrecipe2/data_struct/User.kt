package com.dd2d.talkingrecipe2.data_struct

import android.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    @SerialName("no")
    val no: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("user_name")
    val name: String,
    @SerialName("profile_image_path")
    val profileImagePath: String,
    @SerialName("background_image_path")
    val backgroundImagePath: String,
    @SerialName("recent_recipe_Id")
    val recentRecipeId: String,
)

data class User(
    val userId: String = "",
    val name: String = "",
    val profileImageUri: Uri = Uri.EMPTY,
    val backgroundImageUri: Uri = Uri.EMPTY,
){
    override fun toString(): String {
        return "User(\n" +
                "\tuserId = $userId, \n" +
                "\tname = $name, \n" +
                "\tprofileImageUri = $profileImageUri,\n" +
                "\tbackgroundImageUri = $backgroundImageUri\n)"
    }

    val userFullName = "$name @$userId"
}