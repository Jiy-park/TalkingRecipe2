package com.dd2d.talkingrecipe2.model.user

import android.net.Uri
import androidx.core.net.toUri
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.data_struct.UserDTO
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Expire.In1Hours
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Field.UserFetchColumns
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Filter.UserIdEqualTo
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Filter.UserPasswordEqualTo
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserImageTable
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserLoginTable
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserTable
import com.dd2d.talkingrecipe2.toSupabaseUrl
import com.dd2d.talkingrecipe2.toUriWithDrawable
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

/** 유저 데이터를 데이터베이스로부터 가져옴.
 * 데이터를 받아오는 역할만 수행.
 *- [fetchUserById]
 *- [fetchUserDTOById]
 *- [fetchUserProfileImageUriById]
 *- [fetchUserBackgroundImageUriById]
 * @see UserFetchRepositoryImpl*/
interface UserFetchRepository {
    /**- 유저 아이디에 해당하는 데이터를  [User]형태로 반환.
     * [fetchUserDTOById] + [fetchUserProfileImageUriById] + [fetchUserBackgroundImageUriById] = [User]의 형태로 정의할 것.
     *- [User] 값을 얻기위해 [UserFetchRepository]를 사용할 경우 [fetchUserById]를 통해 얻는다.
     *- 웬만하면 [fetchUserDTOById], [fetchUserProfileImageUriById], [fetchUserBackgroundImageUriById]함수를 직접 호출하지 말 것.*/
    suspend fun fetchUserById(userId: String): User
    /** 유저의 정보를 [UserDTO]형태로 받아옴.*/
    suspend fun fetchUserDTOById(userId: String): UserDTO
    /**- 유저 아이디에 맞는 프로필 이미지를 [Uri]형태로 반환. 저장된 이미지가 없는 경우 [R.drawable.default_image] 사용.*/
    suspend fun fetchUserProfileImageUriById(userId: String, imagePath: String): Uri
    /**- 유저 아이디에 맞는 배경 이미지를 [Uri]형태로 반환. 저장된 이미지가 없는 경우 [R.drawable.default_image] 사용.*/
    suspend fun fetchUserBackgroundImageUriById(userId: String, imagePath: String): Uri
}

/** [UserFetchRepository]의 구현체. 유저의 데이터를 데이터베이스로부터 가져옴.
 * @see UserFetchRepository*/
class UserFetchRepositoryImpl: UserFetchRepository {
    private val database = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ){
        install(Postgrest)
        install(Storage)
    }

    /** [userId] 값이 데이터베이스에 이미 존재하는 값인지 확인.
     * @return [userId]값이 이미 존재하면 -> true.
     *
     * 존재하지 않는 값이면 -> false*/
    suspend fun isExistId(userId: String): Boolean{
        try {
            val res = database.from(UserLoginTable)
                .select(columns = Columns.list(UserIdEqualTo)) {
                    filter {
                        eq(UserIdEqualTo, userId)
                    }
                }
                .data
                .drop(1)
                .dropLast(1)
            return res.isNotBlank()
        }
        catch (e: Exception){
            throw IOException("IOException in isExistId().\nuser id -> $userId.\nmessage -> ${e.message}")
        }
    }

    /** 유저가 입력한 [userId]의 값과 [userPassword]의 조합이 [UserLoginTable]에 존쟈하는 지 판별.
     * @return 존재하는 조합인 경우 true
     *
     * 없는 조합인 경우 false*/
    suspend fun validateUser(userId: String, userPassword: String): Boolean{
        try {
            val res = database.from(UserLoginTable)
                .select(columns = Columns.list(UserPasswordEqualTo)){
                    filter {
                        eq(UserIdEqualTo, userId)
                    }
                }
                .data                           // res = [{"user_password":"value"}]
                .split(":\"")[1]     // res =  value"}]
                .dropLast(3)                 // res = value
            return res == userPassword
        }
        catch (e: Exception){
            throw IOException("IOException in validateUser().\nuser id -> $userId.\nuser password -> $userPassword.\nmessage -> ${e.message}")
        }
    }

//    private fun getUserProfileImagePath(userId: String) = "${userId}_profile.jpeg"
//    private fun getUserBackgroundImagePath(userId: String) = "${userId}_background.jpeg"

    override suspend fun fetchUserById(userId: String): User {
        return try {
            val userDTO = fetchUserDTOById(userId)

            withContext(Dispatchers.IO){
                val userProfileImageUri = async { fetchUserProfileImageUriById(userId, userDTO.profileImagePath) }.await()
                val userBackgroundImageUri = async { fetchUserBackgroundImageUriById(userId, userDTO.backgroundImagePath) }.await()

                User(
                    createdAt = userDTO.createdAt,
                    userId = userId,
                    name = userDTO.name,
                    recentRecipeId = userDTO.recentRecipeId,
                    profileImageUri = userProfileImageUri,
                    backgroundImageUri = userBackgroundImageUri,
                )
            }
        }
        catch (e: Exception){
            throw IOException("IOException in fetchUserById().\nuser id -> $userId.\nmessage -> ${e.message}")
        }
    }

    override suspend fun fetchUserDTOById(userId: String): UserDTO {
        return try {
            database.from(UserTable)
                .select(columns = Columns.list(UserFetchColumns)) {
                    filter {
                        eq(UserIdEqualTo, userId)
                    }
                }
                .decodeSingle<UserDTO>()
        }
        catch (e: Exception){
            throw IOException("IOException in fetchUserDTOById().\nuser id -> $userId.\nmessage -> ${e.message}")
        }
    }

    override suspend fun fetchUserProfileImageUriById(userId: String, imagePath: String): Uri {
        imagePath.ifBlank { return R.drawable.default_image.toUriWithDrawable() }

        return try {
            val res = database.storage
                .from("$UserImageTable/$userId")
                .createSignedUrl(
                    path = imagePath,
                    expiresIn = In1Hours,
                )

            if(res.isBlank()) { R.drawable.default_image.toUriWithDrawable() }
            else { res.toSupabaseUrl().toUri() }
        }
        catch (e: Exception){
            throw IOException("IOException in fetchUserProfileImageUriById().\nuser id -> $userId.\nmessage -> ${e.message}")
        }
    }

    override suspend fun fetchUserBackgroundImageUriById(userId: String, imagePath: String): Uri {
        imagePath.ifBlank { return R.drawable.default_image.toUriWithDrawable() }

        return try {
            val res = database.storage
                .from("$UserImageTable/$userId")
                .createSignedUrl(
                    path = imagePath,
                    expiresIn = In1Hours,
                )

            if(res.isBlank()) { R.drawable.default_image.toUriWithDrawable() }
            else { res.toSupabaseUrl().toUri() }
        }
        catch (e: Exception){
            throw IOException("IOException in fetchUserBackgroundImageUriById().\nuser id -> $userId.\nmessage -> ${e.message}")
        }
    }
}