package com.dd2d.talkingrecipe2.model.recipe

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.R
import com.dd2d.talkingrecipe2.data_struct.SavePostDTO
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfo
import com.dd2d.talkingrecipe2.data_struct.SimpleUserInfoDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.Ingredient
import com.dd2d.talkingrecipe2.data_struct.recipe.IngredientDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfoDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfo
import com.dd2d.talkingrecipe2.data_struct.recipe.StepInfoDTO
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Expires.In30M
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.BasicInfoField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.RecipeVersion
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Field.SavePostField
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Filter.AuthorIdEqualTo
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Filter.RecipeIdEqualTo
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Filter.RecipeVersionEqualTo
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Filter.UserIdEqualTo
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.IngredientTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.RecipeImageTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.RecipeTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.SavePostTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.StepInfoImageTable
import com.dd2d.talkingrecipe2.model.recipe.RecipeDBValue.Table.StepInfoTable
import com.dd2d.talkingrecipe2.model.user.UserDBValue.Field.SimpleUserFetchColumn
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserImageTable
import com.dd2d.talkingrecipe2.model.user.UserDBValue.UserTable
import com.dd2d.talkingrecipe2.toSupabaseUrl
import com.dd2d.talkingrecipe2.toUriWithDrawable
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.io.IOException

/** 레시피와 관련된 데이터를 받아온다.
 * - [fetchRecipeBasicInfoById]
 * - [fetchRecipeIngredientListById]
 * - [fetchRecipeStepInfoListById]
 * - [fetchRecipeThumbnailUriById]
 * - [fetchRecipeAuthorInfo]
 * */
interface RecipeFetchRepository {
    /** [recipeId]에 맞는 레시피의 최근 버전을 받아옴.*/
    suspend fun checkRecipeVersion(recipeId: String): Long
    /** [recipeId]에 맞는 레시피의 [RecipeBasicInfo]를 받아옴.*/
    suspend fun fetchRecipeBasicInfoById(recipeId: String): RecipeBasicInfo
    /** [recipeId]와 [version]의 값이 일치하는 레시피의 [Ingredient] 리스트를 받아옴.*/
    suspend fun fetchRecipeIngredientListById(recipeId: String, version: Long): List<Ingredient>
    /** [recipeId]와 [version]의 값이 일치하는  레시피의 [StepInfo] 리스트를 받아옴.
     * [StepInfoTable]로부터 StepInfoDTO를 리스트 형태로 받은 후
     * 리스트를 순회하며 dto에 저장된 imagePath 값을 이용해 [RecipeImageTable]로부터 맞는 이미지를 가져옴*/
    suspend fun fetchRecipeStepInfoListById(recipeId: String, version: Long): List<StepInfo>
    /** [recipeId]에 맞는 레시피의 썸네일 이미지를 [Uri]형태로 받아옴.*/
    suspend fun fetchRecipeThumbnailUriById(recipeId: String): Uri
    /** [recipeId]에 맞는 레시피의 작성자 정보를 [SimpleUserInfo]형태로 받아옴.*/
    suspend fun fetchRecipeAuthorInfo(recipeId: String): SimpleUserInfo
}

class RecipeFetchRepositoryImpl: RecipeFetchRepository {
    private val database = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ){
        install(Postgrest)
        install(Storage)
    }

    override suspend fun checkRecipeVersion(recipeId: String): Long {
        return try {
            database.from(RecipeTable)
                .select(columns = Columns.list(RecipeVersion)) {
                    filter {
                        eq(RecipeIdEqualTo, recipeId)
                    }
                }
                .data
                .split(":")[1]
                .dropLast(2)
                .toLong()
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "RecipeFetchRepositoryImpl :: checkRecipeVersion() -> $e")
            throw IOException("IOException in checkRecipeVersion().\nrecipe id -> $recipeId    .\nmessage -> ${e.message}")
        }
    }

    /** [userId]의 유저가 작성한 레시피의 [RecipeBasicInfo] 목록을 반환한다.
     * @return [List] of [RecipeBasicInfo]*/
    suspend fun fetchMyRecipeListByUserId(userId: String): List<RecipeBasicInfo>{
        return try {
            database.from(RecipeTable)
                .select(columns = Columns.list(BasicInfoField)){
                    filter {
                        eq(AuthorIdEqualTo, userId)
                    }
                }
                .decodeList<RecipeBasicInfoDTO>()
                .map { it.toRecipeBasicInfo() }

        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "RecipeFetchRepositoryImpl :: fetchMyRecipeListByUserId() -> $e")
            throw IOException("IOException in fetchRecipeIdListByUserId().\nuser id -> $userId.\nmessage -> ${e.message}")
        }
    }

    /** [userId]에 해당하는 유저가 저장한 레시피 게시글등을 [SavePostDTO] 형태의 리스트로 반환
     * @return [List] of [SavePostDTO]*/
    suspend fun fetchSavePostIdListByUserId(userId: String): List<SavePostDTO>{
        return try {
            database.from(SavePostTable)
                .select(columns = Columns.list(SavePostField)){
                    filter {
                        eq(UserIdEqualTo, userId)
                    }
                }
                .decodeList<SavePostDTO>()
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "RecipeFetchRepositoryImpl :: fetchSavePostIdListByUserId() -> $e")
            throw IOException("IOException in fetchSavePostIdListByUserId().\nuser id -> $userId.\nmessage -> ${e.message}")
        }
    }

    override suspend fun fetchRecipeBasicInfoById(recipeId: String): RecipeBasicInfo {
        return try {
            database.from(RecipeTable)
                .select(columns = Columns.list(BasicInfoField)) {
                    filter {
                        eq(RecipeIdEqualTo, recipeId)
                    }
                }
                .decodeSingle<RecipeBasicInfoDTO>()
                .toRecipeBasicInfo()

        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "RecipeFetchRepositoryImpl :: fetchRecipeBasicInfoById() -> $e")
            throw IOException("IOException in fetchRecipeBasicInfoById().\nrecipe id -> $recipeId    .\nmessage -> ${e.message}")
        }
    }


    override suspend fun fetchRecipeIngredientListById(recipeId: String, version: Long): List<Ingredient> {
        return try {
            database.from(IngredientTable)
                .select {
                    filter {
                        and {
                            eq(RecipeIdEqualTo, recipeId)
                            eq(RecipeVersionEqualTo, version)
                        }
                    }
                }
                .decodeList<IngredientDTO>()
                .map{ dto->
                    dto.toIngredient()
                }
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "RecipeFetchRepositoryImpl :: fetchRecipeIngredientListById() -> $e")
            throw IOException("IOException in fetchRecipeIngredientListById().\nrecipe id -> $recipeId.\nmessage -> ${e.message}")
        }
    }

    override suspend fun fetchRecipeStepInfoListById(recipeId: String, version: Long): List<StepInfo> {
        return try {
            database.from(StepInfoTable)
                .select {
                    filter {
                        and {
                            eq(RecipeIdEqualTo, recipeId)
                            eq(RecipeVersionEqualTo, version)
                        }
                    }
                }
                .decodeList<StepInfoDTO>()
                .map { dto->
                    val imageUri = database
                        .storage
                        .from("${RecipeImageTable}/$recipeId/${StepInfoImageTable}")
                        .createSignedUrl(path = dto.imagePath, expiresIn = In30M)
                        .toSupabaseUrl()
                        .toUri()
                    dto.toStepInfo(imageUri = imageUri)
                }
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "RecipeFetchRepositoryImpl :: fetchRecipeStepInfoListById() -> $e")
            throw IOException("IOException in fetchRecipeStepInfoListById().\nrecipe id -> $recipeId.\nmessage -> ${e.message}")
        }
    }


    override suspend fun fetchRecipeThumbnailUriById(recipeId: String): Uri {
        return try {
            database.storage
                .from("${RecipeImageTable}/$recipeId")
                .createSignedUrl(path = "${recipeId}_thumbnail.jpeg", In30M)
                .toSupabaseUrl()
                .toUri()
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "RecipeFetchRepositoryImpl :: fetchRecipeThumbnailUriById() -> $e")
            throw IOException("IOException in fetchRecipeThumbnailUriById().\nrecipe id -> $recipeId.\nmessage -> ${e.message}")
        }
    }

    override suspend fun fetchRecipeAuthorInfo(recipeId: String): SimpleUserInfo {
        return try {
            /** 레시피의 아이디 형태 : {유저 아이디}_{만들어진 시간} */
            val authorId = recipeId.split("_")[0]
            val simpleUserInfoDTO = database
                .from(UserTable)
                .select(columns = Columns.list(SimpleUserFetchColumn)) {
                    filter {
                        eq(UserIdEqualTo, authorId)
                    }
                }
                .decodeSingle<SimpleUserInfoDTO>()

            val authorProfileImageUri =
                if(simpleUserInfoDTO.userProfileImagePath.isNotBlank()){
                    database.storage
                        .from("$UserImageTable/$authorId")
                        .createSignedUrl(path = simpleUserInfoDTO.userProfileImagePath, expiresIn = In30M)
                        .toSupabaseUrl()
                        .toUri()
                }
                else{
                    R.drawable.default_image.toUriWithDrawable()
                }

            SimpleUserInfo(
                userId = authorId,
                userName = simpleUserInfoDTO.userName,
                userProfileImageUri = authorProfileImageUri
            )
        }
        catch (e: Exception){
            Log.e("LOG_CHECK", "RecipeFetchRepositoryImpl :: fetchRecipeAuthorInfo() -> $e")
            throw IOException("IOException in fetchRecipeAuthorInfo().\nrecipe id -> $recipeId.\nmessage -> ${e.message}")
        }
    }
}
