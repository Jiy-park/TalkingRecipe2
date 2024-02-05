package com.dd2d.talkingrecipe2.model.user

import android.net.Uri
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.data_struct.UserDTO
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Field.UserUpsertField
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserLoginTable
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserTable
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.IOException

/** 유저의 정보를 데이터베이스에 업로드함.
 *- [uploadUser]
 *- [uploadUserProfileImage]
 *- [uploadUserBackgroundImage]*/
interface UserUploadRepository {
    /** 유저의 정보를 데이터베이스에 업로드함. */
    suspend fun uploadUser(user: User)
    /** 유저의 프로필 이미지를 업로드함. */
    suspend fun uploadUserProfileImage(image: Uri)
    /** 유저의 배경 이미지를 업로드함.*/
    suspend fun uploadUserBackgroundImage(image: Uri)
}

/** 유저가 로그인 하기 위해 필요한 데이터를 통신하기 위한 DTO.
 * 로그인 관련 작업에만 사용할 것.
 * 1. 로그인
 * 2. 회원 가입 -> [UserUploadRepositoryImpl.insertNewUserToLoginTable]
 * @param userId 유저의 고유값 중 하나. 유저가 로그인 시 사용할 값. [createdAt]와 함계 기본키가 된다.
 * @param userPassword 유저의 비밀번호. 유저가 로그인 시 [userId]와 함께 사용된다.
 * @param createdAt 유저가 회원 가입한 날짜와 시간을 [Long]값으로 변환한 것. 해당 값은 [System.currentTimeMillis]함수를 사용한다.*/
@Serializable
data class UserLoginDTO(
    @SerialName("user_id")
    val userId: String,
    @SerialName("user_password")
    val userPassword: String,
    @SerialName("created_at")
    val createdAt: Long,
)

class UserUploadRepositoryImpl: UserUploadRepository{
    private val database = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ){
        install(Postgrest)
        install(Storage)
    }

    override suspend fun uploadUser(user: User) {
        try {
            val upload = user.toDTO()
            database.from(UserTable)
                .upsert(
                    value = upload,
                    onConflict = UserUpsertField
                )
        }
        catch (e :Exception){
            throw IOException("IOException in UserUploadRepositoryImpl.\nuser : $user.\nerror : $e")
        }
    }

    override suspend fun uploadUserProfileImage(image: Uri) {
        try {

        }
        catch (e :Exception){
            throw IOException("IOException in uploadUserProfileImage.\nimage : $image.\nerror : $e")
        }
    }

    override suspend fun uploadUserBackgroundImage(image: Uri) {
        try {

        }
        catch (e :Exception){
            throw IOException("IOException in uploadUserBackgroundImage.\nimage : $image.\nerror : $e")
        }
    }

    /** 새로운 유저 등록. 해당 함수는 [UserLoginTable], [UserTable]에 새로운 유저를 추가하는 과정이 포함돼 있음.
     * @param onTaskState 현재 진행 중인 과정에 대한 메세지.
     * @see insertNewUserToLoginTable
     * @see insertNewUserToUserTable*/
    suspend fun joinNewUser(
        userId: String,
        userPassword: String,
        userName: String,
        onTaskState: (taskMessage: String)->Unit,
    ): User {
        val createdAt = System.currentTimeMillis()
        onTaskState("joinNewUser()::start insert new user to user login table.")
        insertNewUserToLoginTable(createdAt, userId, userPassword)

        onTaskState("joinNewUser()::start insert new user to user table.")
        insertNewUserToUserTable(createdAt, userId, userName)

        return User(
            userId = userId,
            name = userName,
            createdAt = createdAt,
            recentRecipeId = "",
            profileImageUri = Uri.EMPTY,
            backgroundImageUri = Uri.EMPTY
        )
    }

    /** 유저 로그인 테이블 ( = [UserLoginTable])에 새로운 유저의 정보를 추가함.
     * 해당 값을 통해 유저가 로그인 함.
     * @param createdAt 유저가 회원 가입한 날짜와 시간을 [Long] 형태로 변환한 값. [System.currentTimeMillis]함수를 사용하여 생성.
     * @see UserLoginDTO*/
    private suspend fun insertNewUserToLoginTable(
        createdAt: Long,
        userId: String,
        userPassword: String,
    ){
        try {
            val newUserLoginInfo = UserLoginDTO(
                createdAt = createdAt,
                userId = userId,
                userPassword = userPassword,
            )
            database.from(UserLoginTable).insert(newUserLoginInfo){
            }
        }
        catch (e: Exception){
            throw IOException("IOException in insertNewUserToLoginTable.\n" +
                    "userId: $userId\n" +
                    "userPassword: $userPassword\n" +
                    "error : $e")
        }
    }

    /** 유저 테이블 ( =[UserTable])에 새로운 유저 정보를 추가함.
     * 앱 내에서 사용되는 유저의 정보가 기록됨.
     * @param createdAt 유저가 회원 가입한 날짜와 시간을 [Long]형태로 변환한 값. [System.currentTimeMillis] 사용하여 생성.
     * @see UserDTO*/
    private suspend fun insertNewUserToUserTable(
        createdAt: Long,
        userId: String,
        userName: String,
    ){
        try {
            val newUserInfo = UserDTO(
                createdAt = createdAt,
                userId = userId,
                name = userName,
                recentRecipeId = "",
                profileImagePath = "",
                backgroundImagePath = ""
            )
            database.from(UserTable).insert(newUserInfo)

        }
        catch (e: Exception){
            throw IOException("IOException in insertNewUserToUserTable.\n" +
                    "userId: $userId\n" +
                    "userName: $userName\n" +
                    "error : $e")
        }
    }
}