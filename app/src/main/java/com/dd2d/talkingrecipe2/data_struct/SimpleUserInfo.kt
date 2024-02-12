package com.dd2d.talkingrecipe2.data_struct

import android.net.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 유저의 간한단 정보만 보여줌.
 *
 * **주요 사용처**
 * 1. 레시피의 작성자 정보
 * 2. 친구 정보
 * 3. 검색 결과로 나온 유저 정보
 * */
data class SimpleUserInfo(
    val userId: String,
    val userName: String,
    val userProfileImageUri: Uri
)

/**@see SimpleUserInfo*/
@Serializable
data class SimpleUserInfoDTO(
    @SerialName("user_id")
    val userId: String,
    @SerialName("user_name")
    val userName: String,
    @SerialName("profile_image_path")
    val userProfileImagePath: String
)

