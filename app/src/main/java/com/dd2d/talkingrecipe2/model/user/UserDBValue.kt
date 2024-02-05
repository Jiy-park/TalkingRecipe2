package com.dd2d.talkingrecipe2.model.user

import kotlin.time.Duration.Companion.hours

object UserDBValue{
    const val UserTable = "user"
    const val UserImageTable = "users_image"
    const val UserLoginTable = "user_login"
    object Filter{
        const val UserId = "user_id"
        const val UserPassword = "user_password"


    }
    object Expire{
        val In1Hours = 1.hours
    }

    object Field{
        const val UserUpsertField = "user_id"
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