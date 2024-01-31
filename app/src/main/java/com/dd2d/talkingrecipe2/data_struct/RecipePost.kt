package com.dd2d.talkingrecipe2.data_struct

import android.net.Uri
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo

data class RecipePost(
    val recipeBasicInfo: RecipeBasicInfo,
    val thumbnailImageUri: Uri,
    val author: String
)

data class FriendInfo(
    val userId: String,
    val userName: String,
    val userProfileImageUri: Uri
)
