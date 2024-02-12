package com.dd2d.talkingrecipe2.model.user

import kotlin.time.Duration.Companion.hours

object UserDBValue{
    const val UserTable = "user"
    const val UserImageTable = "users_image"
    const val UserLoginTable = "user_login"
    const val FriendTable = "friend"

    object Columns{
        const val UserProfilePath = "profile_image_path"
        const val UserBackgroundPath = "background_image_path"
        const val RecentRecipeId = "recent_recipe_id"
        const val UserName = "user_name"
    }


    object Filter{
        const val UserIdEqualTo = "user_id"
        const val UserPasswordEqualTo = "user_password"
    }
    object Expire{
        val In1Hours = 1.hours
    }

    object Field{
        const val UserUpsertField = "user_id"
        const val UserId = "user_id"
        const val UserPassword = "user_password"
        const val UserName = "user_name"
        val FriendFetchColumn = listOf(
            "user_id",
            "friend_id"
        )
        val UserFetchColumns = listOf(
            "user_id",
            "user_name",
            "created_at",
            "recent_recipe_id",
            "profile_image_path",
            "background_image_path",
        )
    }
}