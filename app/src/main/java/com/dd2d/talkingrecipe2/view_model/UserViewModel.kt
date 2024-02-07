package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.view_model.UserState.OnLogin
import com.dd2d.talkingrecipe2.view_model.UserState.OnLogout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/** 앱 내에서 사용하는 유저의 상태.
 *- [OnLogout]
 *- [OnLogin] */
sealed class UserState {
    /**- 로그 아웃 상태.
     * 해당 상태일 경우 [Screen.Login]으로 화면 전환.
     * 앱이 시작될 경우 해당 상태로 시작.*/
    object OnLogout: UserState(){
        init { Log.d("LOG_CHECK", "User State : OnLogout -> logout") }
    }

    /** 로그 아웃 상태 중 하나.
     * 로그 아웃 상태에서 데이터베이스로부터 데이터를 가져오거나, 업로드할 때의 상태.*/
    class OnTask(msg: String): UserState(){
        init { Log.d("LOG_CHECK", "User State : OnTask -> $msg") }
    }

    /**- 유저가 [Screen.Login]에서 아이디를 입력하여 로그인한 상태.
     * 로그인 직후엔 [Screen.Main]으로 이동됨.*/
    object OnLogin: UserState(){
        init { Log.d("LOG_CHECK", "User State : OnLogin -> login") }
    }

    class OnError(msg: String): UserState(){
        init { Log.e("LOG_CHECK", "User State : OnError -> error in login task. message -> $msg") }
    }
}


/** 현재 접속 중인 유저*/
class UserViewModel(
    private val userFetchRepo: UserFetchRepositoryImpl,
    private val userUploadRepo: UserUploadRepositoryImpl,
): ViewModel() {
    private var _userState = MutableStateFlow<UserState>(UserState.OnLogout)
    val userState: StateFlow<UserState> get() = _userState.asStateFlow()

    private var _user = MutableStateFlow<User>(User.Empty)
    val user: StateFlow<User> get() = _user.asStateFlow()

    fun updateUser(update: User){
        uploadUserTask(before = _user.value.copy(), after = update)
        _user.value = update
    }

    /** 업데이트된 유저의 정보 중 바뀐 부분만 업로드함.
     * @param before 바뀌기 전의 유저 정보
     * @param after 바뀐 후의 유저 정보*/
    private fun uploadUserTask(before: User, after: User) {
        val userId = before.userId
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(before.name != after.name){
                    async { userUploadRepo.updateUserName(userId, after.name) }.await()
                }
                if(before.recentRecipeId != after.recentRecipeId){
                    async { userUploadRepo.updateUserRecentRecipe(userId, after.recentRecipeId) }.await()
                }
                if(before.profileImageUri != after.profileImageUri){
                    async {
                        userUploadRepo
                            .updateUserProfileImage(
                                userId = userId,
                                image = after.profileImageUri,
                            ){ taskMessage->
                                Log.d("LOG_CHECK", "UserViewModel :: uploadUserTask() -> $taskMessage")
                            }
                    }.await()
                }
                if(before.backgroundImageUri != after.backgroundImageUri){
                    async {
                        userUploadRepo
                            .updateUserBackgroundImage(
                                userId = userId,
                                image = after.backgroundImageUri
                            ){ taskMessage->
                                Log.d("LOG_CHECK", "UserViewModel :: uploadUserTask() -> $taskMessage")
                            }
                    }.await()
                }
            }
            catch (e: Exception){
                _userState.value = UserState.OnError("uploadUserTask():: fail to upload user .\n" +
                        "before -> $before\n" +
                        "after -> $after\n" +
                        "message -> $e")
            }
        }
    }


    init {
        Log.d("LOG_CHECK", "UserViewModel : Init().")
    }

    /** 로그아웃. 기존에 접속 중이던 아이디는 제거됨.*/
    fun logout(){
        _user.value = User.Empty
    }

    fun login(user: User){
        _user.value = user
    }
}