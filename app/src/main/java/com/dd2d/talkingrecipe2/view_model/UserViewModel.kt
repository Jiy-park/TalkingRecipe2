package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.data_struct.RecipePost
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view_model.UserState.Init
import com.dd2d.talkingrecipe2.view_model.UserState.OnError
import com.dd2d.talkingrecipe2.view_model.UserState.OnStable
import com.dd2d.talkingrecipe2.view_model.UserState.OnTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/** 유저의 행동 상태. [UserState]는 UI에 영향을 주지 않으며, 로그를 확인하는 용도로만 사용된다.
 *- [Init]
 *- [OnStable]
 *- [OnTask]
 *- [OnError]*/
sealed class UserState {
    /** 유저가 로그아웃된 상태. 앱이 처음 시작되거나 유저가 로그 아웃했을 경우 해당 상태를 갖는다.*/
    object Init: UserState(){
        init { Log.d("LOG_CHECK", "UserState : Init -> init UserViewModel") }
    }
    /** 평온한 상태. 유저가 특별한 행동을 취하지 않은 상태.
     *- 유저가 로그인 한 경우 [Init] -> [OnStable]
     *- 유저가 로그아웃 한 경우 [OnStable] -> [Init]*/
    class OnStable(msg: String): UserState(){
        init { Log.d("LOG_CHECK", "UserState : OnStable -> $msg") }
    }
    /** 데이터베이스와 통신하는 상태. 유저 데이터의 변경이 일어난다. 에러 발생 시 [OnError]로 전환.*/
    class OnTask(msg: String): UserState(){
        init { Log.d("LOG_CHECK", "UserState : OnTask -> $msg") }
    }
    /** 데이터베이스와 통신 에러가 발생한 상태. */
    class OnError(msg: String): UserState(){
        init { Log.e("LOG_CHECK", "UserState : OnError -> error in login task. message -> $msg") }
    }
}


/** 현재 접속 중인 유저의 정보를 다루는 뷰모델. */
class UserViewModel(
    private val recipeFetchRepo: RecipeFetchRepositoryImpl,
    private val recipeUploadRepo: RecipeUploadRepositoryImpl,
    private val userFetchRepo: UserFetchRepositoryImpl,
    private val userUploadRepo: UserUploadRepositoryImpl,
): ViewModel() {
    /** 유저의 행동 상태. */
    private var _userState = MutableStateFlow<UserState>(UserState.Init)

    private var _user = MutableStateFlow<User>(User.Empty)
    val user: StateFlow<User> get() = _user.asStateFlow()

    private var _friendList = MutableStateFlow<List<SimpleUserInfo>>(emptyList())
    val friendList: StateFlow<List<SimpleUserInfo>> get() = _friendList.asStateFlow()

    private var _myPostList = MutableStateFlow<List<RecipePost>>(emptyList())
    val myPostList: StateFlow<List<RecipePost>> get() = _myPostList.asStateFlow()

    private var _savePostList = MutableStateFlow<List<RecipePost>>(emptyList())
    val savePostList: StateFlow<List<RecipePost>> get() = _savePostList.asStateFlow()

    /** 유저가 작성한 레시피를 불러온다.*/
    private fun fetchMyPost(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _userState.value = OnTask("fetchMyPost()::start fetch user's post list.")
                val myPostIdList = recipeFetchRepo.fetchMyRecipeListByUserId(_user.value.userId)
                _myPostList.value = myPostIdList.mapIndexed { index, recipeBasicInfo ->
                    async {
                        _userState.value = OnTask("fetchMyPost()::start fetch [$index]. post")

                        val thumbnailImageUri = recipeFetchRepo.fetchRecipeThumbnailUriById(recipeBasicInfo.recipeId)
                        val authorId = recipeBasicInfo.authorId
                        val authorName = userFetchRepo.fetchUserNameById(authorId)

                        _userState.value = OnTask("fetchMyPost()::finished fetch [$index]. post")

                        RecipePost(
                            recipeBasicInfo = recipeBasicInfo,
                            thumbnailImageUri = thumbnailImageUri,
                            author = "$authorName @$authorId"
                        )
                    }
                }.awaitAll()
                _userState.value = OnStable("fetchMyPost()::finished fetch user's post list. list size : ${_myPostList.value.size}")
            }
            catch (e: Exception){
                _userState.value = OnError("fetchMyPost()::fail to fetch user's post.\nmessage -> $e")
            }
        }
    }

    /** 유저의 친구 목록을 불러온다.*/
    private fun fetchFriend(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _userState.value = UserState.OnTask("fetchFriend()::start fetch user's friend list.")
                val friendIdList = userFetchRepo.fetchFriendListByUserId(_user.value.userId)

                _friendList.value = friendIdList.mapIndexed { index, friendDTO ->
                    async {
                        val friendId = friendDTO.friendId
                        _userState.value = UserState.OnTask("fetchFriend()::start fetch [$index]. friend")
                        val profileImageUri = userFetchRepo.fetchUserProfileImageUriById(userId = friendId)
                        val friendName = userFetchRepo.fetchUserNameById(friendId)
                        _userState.value = UserState.OnTask("fetchFriend()::finished fetch [$index]. friend")
                        SimpleUserInfo(
                            userId = friendId,
                            userName = friendName,
                            userProfileImageUri = profileImageUri
                        )
                    }
                }.awaitAll()
                _userState.value = UserState.OnStable("fetchFriend()::finished fetch user's friend list. list size : ${_friendList.value.size}")
            }
            catch (e: Exception){
                _userState.value = UserState.OnError("fetchFriend()::fail to fetch user's friend.\nmessage -> $e")
            }
        }
    }

    /** 유저가 저장한 레시피 목록을 불러옴.*/
    private fun fetchSavePost() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _userState.value = UserState.OnTask("fetchSavePost()::start fetch user's save post list.")
                val savePostList = recipeFetchRepo.fetchSavePostIdListByUserId(_user.value.userId)

                _savePostList.value = savePostList.mapIndexed { index, savePostDTO ->
                    async {
                        _userState.value = UserState.OnTask("fetchSavePost()::start fetch [$index]. post")

                        val savePostId = savePostDTO.recipeId

                        val recipeBasicInfo = recipeFetchRepo.fetchRecipeBasicInfoById(savePostId)
                        val thumbnailImageUri = recipeFetchRepo.fetchRecipeThumbnailUriById(savePostId)
                        val authorId = recipeBasicInfo.authorId
                        val authorName = userFetchRepo.fetchUserNameById(authorId)
                        _userState.value = UserState.OnTask("fetchSavePost()::finished fetch [$index]. post")

                        RecipePost(
                            recipeBasicInfo = recipeBasicInfo,
                            thumbnailImageUri = thumbnailImageUri,
                            author = "$authorName @$authorId"
                        )
                    }
                }.awaitAll()
                _userState.value = UserState.OnStable("fetchSavePost()::finished fetch user's save post list. list size : ${_savePostList.value.size}")
            }
            catch (e: Exception){
                _userState.value = UserState.OnError("fetchSavePost()::fail to fetch user's save post.\nmessage -> $e")
            }
        }
    }

    /** 유저가 로그인 시 필요한 데이터를 받아온다.
     * @see fetchMyPost*/
    private fun init(){
        viewModelScope.launch {
            async { fetchMyPost() }
            async { fetchFriend() }
            async { fetchSavePost() }

        }
    }

    /** 유저의 정보를 업데이트함.
     * 변경된 데이터만 데이터베이스에 업로드한다.
     * @see uploadUserTask*/
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
                    async { userUploadRepo.updateUserName(userId, after.name) }
                }
                if(before.recentRecipeId != after.recentRecipeId){
                    async { userUploadRepo.updateUserRecentRecipe(userId, after.recentRecipeId) }
                }
                if(before.profileImageUri != after.profileImageUri){
                    async {
                        userUploadRepo
                            .updateUserProfileImage(
                                userId = userId,
                                image = after.profileImageUri,
                            ){ taskMessage->
                                _userState.value = UserState.OnTask(taskMessage)
                            }
                    }
                }
                if(before.backgroundImageUri != after.backgroundImageUri){
                    async {
                        userUploadRepo
                            .updateUserBackgroundImage(
                                userId = userId,
                                image = after.backgroundImageUri
                            ){ taskMessage->
                                _userState.value = UserState.OnTask(taskMessage)
                            }
                    }
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

    /** 로그아웃. 기존에 접속 중이던 아이디는 제거됨.*/
    fun logout(){
        _user.value = User.Empty
        _userState.value = UserState.Init
    }

    fun login(user: User){
        _user.value = user
        _userState.value = UserState.OnStable("login()::user logged in. user : $user")
        init()
    }
}