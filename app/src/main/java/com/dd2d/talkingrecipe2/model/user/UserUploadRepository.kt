package com.dd2d.talkingrecipe2.model.user

import android.net.Uri
import android.util.Log
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.data_struct.UserDTO
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Columns.RecentRecipeId
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Columns.UserBackgroundPath
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Columns.UserName
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Columns.UserProfilePath
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Field.UserUpsertField
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Filter.UserIdEqualTo
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserImageTable
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserLoginTable
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserTable
import com.dd2d.talkingrecipe2.uploadImage
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.IOException

/** 유저의 정보를 데이터베이스에 업로드함.
 *- [uploadUser]
 *- [updateUserProfileImage]
 *- [updateUserBackgroundImage]*/
interface UserUploadRepository {
    /** 유저의 정보를 데이터베이스에 업로드함. */
    suspend fun uploadUser(user: User)
    /** 유저의 이름을 업데이트함.*/
    suspend fun updateUserName(userId: String, name: String)
    /** 유저가 최근에 본 레시피 정보를 업데이트함.*/
    suspend fun updateUserRecentRecipe(userId: String, recipeId: String)
    /** 유저의 프로필 이미지를 업데이트함. 해당 과정은 스토리지와 데이터베이스 모두 업데이트 함.*/
    suspend fun updateUserProfileImage(userId: String, image: Uri, onTask: (msg: String) -> Unit)
    /** 유저의 배경 이미지를 업데이트함. 해당 과정은 스토리지와 데이터베이스 모두 업데이트 함.*/
    suspend fun updateUserBackgroundImage(userId: String, image: Uri, onTask: (msg: String) -> Unit)
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
            Log.e("LOG_CHECK", "UserUploadRepositoryImpl :: uploadUser() -> $e")
            throw IOException("IOException in UserUploadRepositoryImpl.\nuser : $user.\nerror : $e")
        }
    }

    override suspend fun updateUserName(userId: String, name: String) {
        try {
            database.from(UserTable).update(
                update = {
                    set(UserName, name)
                },
                request = {
                    filter {
                        eq(UserIdEqualTo, userId)
                    }
                }
            )
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "UserUploadRepositoryImpl :: updateUserName() -> $e")
            throw IOException("IOException in updateUserName.\nuser id : $userId.\nuse name : $name.\nerror : $e")
        }
    }

    override suspend fun updateUserRecentRecipe(userId: String, recipeId: String) {
        try{
            database.from(UserTable).update(
                update = {
                    set(RecentRecipeId, recipeId)
                },
                request = {
                    filter {
                        eq(UserIdEqualTo, userId)
                    }
                }
            )
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "UserUploadRepositoryImpl :: updateUserRecentRecipe() -> $e")
            throw IOException("IOException in updateUserRecentRecipe.\nuser id : $userId.\nrecipe id : $recipeId.\nerror : $e")
        }
    }

//        TODO("유저 프로필 이미지 경로에 대해 생각해보기")
    override suspend fun updateUserProfileImage(
        userId: String,
        image: Uri,
        onTask: (msg: String) -> Unit
    ) {
        try {
            val bucketApi = database.storage.from("$UserImageTable/$userId")
            val uploadPath = "profile.jpeg"

            onTask("updateUserProfileImage()::start update user profile path")
            database.from(UserTable).update(
                update = {
                    set(UserProfilePath, uploadPath)
                },
                request = {
                    filter {
                        eq(UserIdEqualTo, userId)
                    }
                }
            )
            onTask("updateUserProfileImage()::finished update user profile path")
            uploadImage(
                bucketApi = bucketApi,
                uploadPath = "profile.jpeg",
                imageUri = image,
                callFrom = "uploadUserProfileImage",
                onTask = { msg-> onTask(msg) }
            )
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "UserUploadRepositoryImpl :: updateUserProfileImage() -> $e")
            throw IOException("IOException in updateUserProfileImage.\nuser id : $userId.\nimage uri : $image.\nerror : $e")
        }
    }

    override suspend fun updateUserBackgroundImage(
        userId: String,
        image: Uri,
        onTask: (msg: String) -> Unit
    ) {
        try {
            val bucketApi = database.storage.from("$UserImageTable/$userId")
            val uploadPath = "background.jpeg"

            onTask("updateUserProfileImage()::start update user profile path")
            database.from(UserTable).update(
                update = {
                    set(UserBackgroundPath, uploadPath)
                },
                request = {
                    filter {
                        eq(UserIdEqualTo, userId)
                    }
                }
            )
            onTask("updateUserProfileImage()::finished update user profile path")

            uploadImage(
                bucketApi = bucketApi,
                uploadPath = uploadPath,
                imageUri = image,
                callFrom = "uploadUserBackgroundImage",
                onTask = { msg-> onTask(msg) }
            )
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "UserUploadRepositoryImpl :: updateUserBackgroundImage() -> $e")
            throw IOException("IOException in updateUserBackgroundImage.\nuser id : $userId.\nimage uri : $image.\nerror : $e")
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
        try {
            val createdAt = System.currentTimeMillis()
            onTaskState("joinNewUser()::start insert new user to user login table.")
            insertNewUserToLoginTable(createdAt, userId, userPassword)

            onTaskState("joinNewUser()::start insert new user to user table.")
            insertNewUserToUserTable(createdAt, userId, userName)

            return User.Empty.copy(
                userId = userId,
                name = userName,
                createdAt = createdAt,
            )
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "UserUploadRepositoryImpl :: joinNewUser() -> $e")
            throw IOException("IOException in joinNewUser.\nuser id : $userId.\nuser password : $userPassword\nuser name : $userName\nerror : $e")
        }
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
            Log.e("LOG_CHECK", "UserUploadRepositoryImpl :: insertNewUserToLoginTable() -> $e")
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
            Log.e("LOG_CHECK", "UserUploadRepositoryImpl :: insertNewUserToUserTable() -> $e")
            throw IOException("IOException in insertNewUserToUserTable.\n" +
                    "userId: $userId\n" +
                    "userName: $userName\n" +
                    "error : $e")
        }
    }
}