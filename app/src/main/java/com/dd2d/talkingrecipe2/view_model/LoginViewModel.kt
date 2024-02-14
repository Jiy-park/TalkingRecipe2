package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserLoginTable
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.view.login_screen.LoginScreen
import com.dd2d.talkingrecipe2.view_model.LoginState.OnError
import com.dd2d.talkingrecipe2.view_model.LoginState.OnLogout
import com.dd2d.talkingrecipe2.view_model.LoginState.OnStable
import com.dd2d.talkingrecipe2.view_model.LoginState.OnTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** 로그인 화면에서의 상태.
 *- [OnStable]
 *- [OnLogout]
 *- [OnTask]
 *- [OnError]
 *
 * @see LoginViewModel
 * @see Screen.Login
 * @see LoginScreen*/
sealed class LoginState {
    /** [LoginState]의 기본적인 상태. [LoginScreen]에서 별 다른 작업이 없으면 해당 상태로 유지된다.*/
    class OnStable(msg: String): LoginState(){
        init { Log.d("LOG_CHECK", "LoginState : OnStable -> $msg") }
    }
    /** 로그아웃 상태. 1회성 상태. 해당 상태에 진입한 다음 [OnStable] 상태로 바뀐다.*/
    class OnLogout(msg: String): LoginState(){
        init { Log.d("LOG_CHECK", "LoginState : OnLogout -> $msg") }
    }
    /** 로그인 상태. 1회성 상태. 해당 상태에 진입한 다음 [OnStable] 상태로 바뀐다.*/
    class OnLogin(msg: String): LoginState(){
        init { Log.d("LOG_CHECK", "LoginState : OnLogin -> $msg") }
    }
    /** 데이터베이스와 통신하는 상태. 로그인에 관련된 데이터를 주거나 받음.*/
    class OnTask(msg: String): LoginState(){
        init { Log.d("LOG_CHECK", "LoginState : OnTask -> $msg") }
    }
    /** 로그인에 관련된 데이터를 받아오던 중 에러가 발생한 상태.*/
    class OnError(msg: String): LoginState(){
        init { Log.e("LOG_CHECK", "LoginState : OnError -> $msg") }
    }
}

/** 유저가 앱에 로그인 할 때 사용.
 * @param userFetchRepo 로그인에 관련된 유저의 정보를 가져옴.
 * @param userUploadRepo 유저가 회원 가입 시 데이터를 업로드함.*/
class LoginViewModel(
    private val userFetchRepo: UserFetchRepositoryImpl,
    private val userUploadRepo: UserUploadRepositoryImpl,
): ViewModel() {
    private var _loginState = MutableStateFlow<LoginState>(LoginState.OnStable("init LoginViewModel."))
    val loginState: StateFlow<LoginState> get() = _loginState.asStateFlow()

    fun logout(user: User){
        _loginState.value = LoginState.OnLogout("logged out. user : $user")
    }

    /** 로그인에 필요한 유저의 정보를 가져옴.
     * [tryLogin]가 선행되어야 하며, [tryLogin] 결과가 true일 때만 실행되어야 함.
     * @see tryLogin*/
    suspend fun fetchUserById(userId: String): User{
        return withContext(Dispatchers.IO){
            try {
                _loginState.value = LoginState.OnTask("login()::start fetch user data for login. user id : $userId.")
                val user = userFetchRepo.fetchUserById(userId)
                _loginState.value = LoginState.OnLogin("login()::finished fetch user data for login. success login.")
                user
            }
            catch (e: Exception){
                _loginState.value = LoginState.OnTask("login()::fail to fetch data for login.\n" +
                        "userId -> $userId\n" +
                        "message -> $e")
                User.Empty
            }
        }
    }


    /** [userId] + [userPassword] 조합이 [UserLoginTable]에 존재하는 지 확인.
     * @return 존재하는 값인 경우 true.
     *
     * 존재하지 않는 값인 경우 false
     * @see fetchUserById*/
    suspend fun tryLogin(userId: String, userPassword: String): Boolean{
        return withContext(Dispatchers.IO){
            try {
                _loginState.value = LoginState.OnTask("tryLogin()::start validate user id and password.\nuser id : $userId.\nuser password : $userPassword")
                if(userFetchRepo.validateUser(userId, userPassword)){
                    _loginState.value = LoginState.OnStable("tryLogin()::finished check user id and password. result : pass")
                    true
                }
                else {
                    _loginState.value = LoginState.OnStable("tryLogin()::finished check user id and password. result : fail")
                    false
                }
            }
            catch (e: Exception){
                _loginState.value = LoginState.OnError("tryLogin()::fail to try login.\n" +
                        "user id : $userId.\n" +
                        "user password : $userPassword.\n" +
                        "message : $e")
                false
            }
        }
    }

    /** 입력받은 값을 통해 새로운 유저 생성.
     * 회원가입 이후 로그인을 할 때는 [loginWithJoin] 사용한다.
     * @param userId 새로운 유저가 사용할 아이디. 해당 값은 데이터베이스에서 유일해야 함.
     * @param userPassword 유저의 비밀번호.
     * @param userName 유저의 이름. 해당 값은 조건 없이 변경가능함.*/
    fun joinNewUser(
        userId: String,
        userPassword: String,
        userName: String,
        onEndTask: (User)->Unit,
    ){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userUploadRepo
                    .joinNewUser(
                        userId = userId,
                        userPassword = userPassword,
                        userName = userName
                    ){ taskMessage->
                        _loginState.value = LoginState.OnTask(taskMessage)
                    }
                onEndTask(user)
            }
            catch (e: Exception){
                _loginState.value = LoginState.OnError("joinNewUser()::fail to insert new user.\n" +
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
        return try{
            _loginState.value = LoginState.OnTask("checkDuplicateUserId()::start fetch user data for check id duplicate. user id : $userId")
            val isDuplicate = userFetchRepo.isExistId(userId)
            _loginState.value = LoginState.OnStable("checkDuplicateUserId()::finished fetch user data for check id duplicate.\n" +
                    "user id : $userId\n" +
                    "result : $isDuplicate.")
            isDuplicate
        }
        catch (e: Exception){
            _loginState.value = LoginState.OnError("checkDuplicateUserId()::fail to fetch data for check id duplicate.\n" +
                    "user id : $userId.\n" +
                    "message : $e")
            false
        }
    }
}