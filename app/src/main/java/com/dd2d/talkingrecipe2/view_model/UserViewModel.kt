package com.dd2d.talkingrecipe2.view_model

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.alog
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.data_struct.UserDTO
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.data_struct.sampleUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.SupabaseClientBuilder
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale.filter


/** 현재 접속 중인 유저*/
class UserViewModel(
    private val userId: String
): ViewModel() {
    private lateinit var database: SupabaseClient
    private var _user by mutableStateOf<User>(sampleUser)
    val user: User
        get() = _user



    var recentRecipeBasicInfo by mutableStateOf(RecipeBasicInfo())
    var recentRecipeAuthor by mutableStateOf("")
    var recentRecipeThumbnail by mutableStateOf(Uri.EMPTY)


    fun init(){
        database = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ){
            install(Postgrest)
            install(Storage)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchUserData()
        }
    }

    private suspend fun fetchUserData(){
        _user = database
            .from("user")
            .select {
                filter {
                    eq("userId", userId)
                }
            }.decodeSingle()
    }

}