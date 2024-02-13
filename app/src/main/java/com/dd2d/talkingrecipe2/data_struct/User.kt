package com.dd2d.talkingrecipe2.data_struct

import android.net.Uri
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.toUriWithDrawable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 데이터베이스로부터 유저의 정보를 통신하기 위한 DTO.
 * @param userId 데이터베이스 내의 유저에서 고유한 값.
 * @param name 유저의 이름.
 * @param createdAt 유저가 회원 가입한 날짜와 시간을 [Long]값으로 변환한 것. [System.currentTimeMillis] 사용하여 생성.
 * @param recentRecipeId 유저가 최근 본 레시피의 아이디. 최근 본 레시피가 없는 경우 공백의 문자열( ="").
 * @param profileImagePath 유저의 프로필 이미지 저장 경로. 이미지의 확장자를 파악하기 위해 사용된다. profile.jpeg 형태로 저장됨.
 * 처음 회원 가입 시에는 공백의 문자열( = "")로 저장된다. ->
 * @param backgroundImagePath 유저의 배경 이미지 저장 경로. 이미지의 확장자를 파악하기 위해 사용된다. profile.jpeg 형태로 저장됨.
 * 처음 회원 가입 시에는 공백의 문자열( = "")로 저장된다. */
@Serializable
data class UserDTO(
    @SerialName("user_id")
    val userId: String,
    @SerialName("user_name")
    val name: String,
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("recent_recipe_id")
    val recentRecipeId: String,
    @SerialName("profile_image_path")
    val profileImagePath: String,
    @SerialName("background_image_path")
    val backgroundImagePath: String,
)

data class User(
    val userId: String,
    val name: String,
    val createdAt: Long,
    val recentRecipeId: String,
    val profileImageUri: Uri,
    val backgroundImageUri: Uri,
){
    override fun toString(): String {
        return "User(\n" +
                "\tuserId = $userId, \n" +
                "\tname = $name, \n" +
                "\tno = $createdAt, \n" +
                "\trecentRecipeId = $recentRecipeId, \n" +
                "\tprofileImageUri = $profileImageUri, \n" +
                "\tbackgroundImageUri = $backgroundImageUri)"
    }

    val userFullName = "$name @$userId"

    fun toDTO(
        profileImagePath: String = "",
        backgroundImagePath: String = ""
    ) = UserDTO(
        createdAt= this.createdAt,
        userId = this.userId,
        name = this.name,
        recentRecipeId = this.recentRecipeId,
        profileImagePath = profileImagePath,
        backgroundImagePath = backgroundImagePath
    )

    fun toSimpleUserInfo() = SimpleUserInfo(
        userId = this.userId,
        userName = this.name,
        userProfileImageUri = this.profileImageUri
    )

    companion object{
        val Empty = User(
            userId = "",
            name = "",
            createdAt = 0L,
            recentRecipeId = "",
            profileImageUri = R.drawable.default_image.toUriWithDrawable(),
            backgroundImageUri = R.drawable.default_image.toUriWithDrawable(),
        )
    }
}