package com.dd2d.talkingrecipe2.view_model

import androidx.lifecycle.ViewModel
import com.dd2d.talkingrecipe2.data_struct.RecipePost
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.logging
import com.dd2d.talkingrecipe2.model.recipe.RecipeFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.recipe.RecipeUploadRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserFetchRepositoryImpl
import com.dd2d.talkingrecipe2.model.user.UserUploadRepositoryImpl
import com.dd2d.talkingrecipe2.view.sub_screen.SubScreen
import com.dd2d.talkingrecipe2.view_model.SubUiState.OnError
import com.dd2d.talkingrecipe2.view_model.SubUiState.OnFetching
import com.dd2d.talkingrecipe2.view_model.SubUiState.OnStable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext


/** [SubScreen]의 UI 상태.
 * * [OnFetching]
 * * [OnStable]
 * * [OnError]*/
sealed class SubUiState {
    class OnFetching(msg: String): SubUiState()
    class OnError(val msg: String): SubUiState()
    object OnStable: SubUiState()
}


class SubViewModel(
    private val recipeFetchRepo: RecipeFetchRepositoryImpl,
    private val recipeUploadRepo: RecipeUploadRepositoryImpl,
    private val userFetchRepo: UserFetchRepositoryImpl,
    private val userUploadRepo: UserUploadRepositoryImpl,
): ViewModel() {
    private var _uiState = MutableStateFlow<SubUiState>(OnStable)
    val uiState: StateFlow<SubUiState> get() = _uiState.asStateFlow()

    /** [SubScreen]의 뷰에서 코드 진행 중 [_uiState] 값의 변경이 필요할 시 호출.*/
    fun requestState(state: SubUiState){
        _uiState.value = state
    }

    init {
        logging("init sub view model")
    }

    /**서브 메인에서 보이는 유저의 정보.
     * [userId]에 맞는 유저의 정보를 [User] 형태로 받아온다.*/
    suspend fun fetchUserInfoById(userId: String): User{
        return try {
            _uiState.value = OnFetching("fetchUserInfoById()::start fetch user data. user id -> $userId")
            userFetchRepo.fetchUserById(userId)
        }
        catch (e: Exception){
            _uiState.value = OnError("fetchUserInfoById()::fail to fetch user data. user id -> $userId")
            User.Empty
        }
    }

    /** [userId]에 맞는 유저가 작성한 게시글을 [RecipePost]형태로 받아온다.
     * @return [List] of [RecipePost]*/
    suspend fun fetchMyPostListByUserId(userId: String): List<RecipePost>{
        return withContext(Dispatchers.IO){
            try {
                _uiState.value = OnFetching("fetchUserPostByUserId()::start fetch user's post list. user id -> $userId")
                val basicInfoList = recipeFetchRepo.fetchMyRecipeListByUserId(userId)
                val userPostList = basicInfoList.map { basicInfo->
                    async{
                        val thumbnailImageUri =
                            recipeFetchRepo.fetchRecipeThumbnailUriById(basicInfo.recipeId)
                        val authorId = basicInfo.authorId
                        val authorName = userFetchRepo.fetchUserNameById(authorId)

                        RecipePost(
                            recipeBasicInfo = basicInfo,
                            thumbnailImageUri = thumbnailImageUri,
                            author = "$authorName @$authorId"
                        )
                    }
                }.awaitAll()
                _uiState.value = OnStable
                userPostList
            }
            catch (e: Exception){
                _uiState.value = OnError("fetchUserPostByUserId()::fail to fetch user's post list. user id -> $userId")
                emptyList()
            }
        }
    }

    /** [userId]에 맞는 유저의 친구목록을 [SimpleUserInfo]형태로 받아온다.
     * @return [List] of [SimpleUserInfo]*/
    suspend fun fetchFriendListByUserId(userId: String): List<SimpleUserInfo>{
        return withContext(Dispatchers.IO){
            try {
                _uiState.value = OnFetching("fetchFriendListByUserId()::start fetch user friend list. user id -> $userId")
                val friendDTOList = userFetchRepo.fetchFriendListByUserId(userId)
                val friendList = friendDTOList.map { friendDTO->
                    async {
                        val friendId = friendDTO.friendId
                        val profileImageUri = userFetchRepo.fetchUserProfileImageUriById(userId = friendId)
                        val friendName = userFetchRepo.fetchUserNameById(friendId)
                        SimpleUserInfo(
                            userId = friendId,
                            userName = friendName,
                            userProfileImageUri = profileImageUri
                        )
                    }
                }.awaitAll()
                _uiState.value = OnStable
                friendList
            }
            catch (e: Exception){
                _uiState.value = OnError("fetchFriendListByUserId()::fail to fetch user friend list. user id -> $userId")
                emptyList()
            }
        }
    }
    /** [userId]에 맞는 유저의 보관함 목록을 [RecipePost]형태로 받아론다.
     * @return [List] of [RecipePost]*/
    suspend fun fetchSavePostListByUserId(userId: String): List<RecipePost>{
        return withContext(Dispatchers.IO){
            try {
                _uiState.value = OnFetching("fetchSavePostListByUserId()::start fetch user save post list. user id -> $userId")
                val savePostIdList = recipeFetchRepo.fetchSavePostIdListByUserId(userId)
                val savePostList = savePostIdList.map { savePostDTO->
                    async {
                        val savePostId = savePostDTO.recipeId

                        val recipeBasicInfo = recipeFetchRepo.fetchRecipeBasicInfoById(savePostId)
                        val thumbnailImageUri = recipeFetchRepo.fetchRecipeThumbnailUriById(savePostId)
                        val authorId = recipeBasicInfo.authorId
                        val authorName = userFetchRepo.fetchUserNameById(authorId)

                        RecipePost(
                            recipeBasicInfo = recipeBasicInfo,
                            thumbnailImageUri = thumbnailImageUri,
                            author = "$authorName @$authorId"
                        )
                    }
                }.awaitAll()
                _uiState.value = OnStable
                savePostList
            }
            catch (e: Exception){
                _uiState.value = OnError("fetchSavePostListByUserId()::fail to fetch user save post list. user id -> $userId")
                emptyList()
            }
        }
    }

}