package com.dd2d.talkingrecipe2.data_struct

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val userId: Int,
    val id: String,
    val name: String,
)

data class User(
    val userId: Int = -1,
    val id: String = "",
    val name: String = "",
    val recentRecipeId: Int,
){
    val nickname: String
        get() = "$name @$id"
}
val sampleUser = User(
    userId = 2_000_001,
    id = "아이디",
    name = "이름",
    recentRecipeId = 1_000_001
)