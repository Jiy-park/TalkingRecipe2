package com.dd2d.talkingrecipe2.data_struct

import android.net.Uri
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class RecipePost(
    val recipeBasicInfo: RecipeBasicInfo,
    val thumbnailImageUri: Uri,
    val author: String
)


@Serializable
data class FriendInfoDTO(
    @SerialName("user_id")
    val userId: String,
    @SerialName("friend_id")
    val friendId: String
)

@Serializable
data class SavePostDTO(
    @SerialName("user_id")
    val userId: String,
    @SerialName("recipe_id")
    val recipeId: String,
)