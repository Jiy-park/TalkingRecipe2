package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserLoginTable
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.view_model.UserState.OnLogin
import com.dd2d.talkingrecipe2.view_model.UserState.OnLogout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/** 앱 내에서 사용하는 유저의 상태.
 *- [OnLogout]
 *- [OnLogin] */
sealed class UserState {
    /**- 로그 아웃 상태.
     * 해당 상태일 경우 [Screen.Login]으로 화면 전환.
     * 앱이 시작될 경우 해당 상태로 시작.*/
    object OnLogout: UserState(){
        init {
            Log.d("LOG_CHECK", "User State : OnLogout -> logout")
        }
    }
    /** 로그 아웃 상태 중 하나.
     * 로그 아웃 상태에서 데이터베이스로부터 데이터를 가져오거나, 업로드할 때의 상태.*/
    class OnTask(msg: String): UserState(){
        init {
            Log.d("LOG_CHECK", "User State : OnTask -> $msg")
        }
    }
    /**- 유저가 [Screen.Login]에서 아이디를 입력하여 로그인한 상태.
     * 로그인 직후엔 [Screen.Main]으로 이동됨.
     * @param user 로그인한 유저의 정보. [Flow]형태로 존재함.*/
    class OnLogin(user: Flow<User>): UserState(){
        init {
            Log.d("LOG_CHECK", "User State : OnLogin -> login user -> $user")
        }
    }

    class OnError(msg: String): UserState(){
        init {
            Log.e("LOG_CHECK", "User State : OnError -> error in login task. message -> $msg")
        }
    }
}


/** 현재 접속 중인 유저*/
class UserViewModel(
    private val userFetchRepo: UserFetchRepositoryImpl,
    private val userUploadRepo: UserUploadRepositoryImpl,
): ViewModel() {
    private var _userState = MutableStateFlow<UserState>(UserState.OnLogout)
    val userState: StateFlow<UserState> get() = _userState.asStateFlow()

    init {
        Log.d("LOG_CHECK", "UserViewModel : Init().")
    }

    /** 로그아웃. 기존에 접속 중이던 아이디는 제거됨.*/
    fun logout(){
        _userState.value = UserState.OnLogout
    }


    /** [userId] + [userPassword]조합이 [UserLoginTable]에 존재하는 지 확인.
     * @return 존재하는 값인 경우 true. 이후 로그인 과정 진행.
     *
     * 존재하지 않는 값인 경우 false
     * @see login*/
    suspend fun tryLogin(userId: String, userPassword: String): Boolean{
        return withContext(Dispatchers.IO){
            if(userFetchRepo.validateUser(userId, userPassword)){
                login(userId)
                true
            }
            else {
                false
            }
        }
    }


    /** [userId]에 해당하는 유저의 정보를 가져옴.
     * [tryLogin] 결과가 true일 때만 실행됨.
     * @see tryLogin*/
    private fun login(userId: String){
        _userState.value = UserState.OnTask("login() : fetch user data for login. user id -> $userId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = flowOf(userFetchRepo.fetchUserById(userId))
                _userState.value = UserState.OnLogin(user)
            }
            catch (e: Exception){
                _userState.value = UserState.OnError("login():: fail to login.\n" +
                        "userId -> $userId\n" +
                        "message -> $e")
            }
        }
    }

    /** 회원 가입에 입력된 값을 바탕으로 로그인.
     * @param user 회원 가입 때 입력된 값.
     * @see joinNewUserWithLogin*/
    private fun login(user: User){
        _userState.value = UserState.OnLogin(flowOf(user))
    }

    /** 입력받은 값을 통해 새로운 유저 생성. 생성 후에는 로그인.
     * @param userId 새로운 유저가 사용할 아이디. 해당 값은 데이터베이스에서 유일해야 함.
     * @param userPassword 유저의 비밀번호.
     * @param userName 유저의 이름. 해당 값은 조건 없이 변경가능함.*/
    fun joinNewUserWithLogin(
        userId: String,
        userPassword: String,
        userName: String
    ){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userUploadRepo
                    .joinNewUser(
                        userId = userId,
                        userPassword = userPassword,
                        userName = userPassword
                    ){ taskMessage->
                        _userState.value = UserState.OnTask(taskMessage)
                    }

                login(user)
            }
            catch (e: Exception){
                _userState.value = UserState.OnError("joinNewUser():: fail to insert new user.\n" +
                        "userId -> $userId\n" +
                        "userPassword -> $userPassword\n" +
                        "userName -> $userName\n" +
                        "message -> $e")
            }
        }
    }

    /** [userId]가 이미 회원가입된 아이디인지 확인함.
     * @return 이미 존재하는 아이디인 경우 true
     *
     * 데이터베이스에 존재하지 않는 아이디인 경우 false*/
    suspend fun checkDuplicateUserId(userId: String): Boolean{
        return withContext(Dispatchers.IO){
            userFetchRepo.isExistId(userId)
        }
    }
}