package com.dd2d.talkingrecipe2.view_model

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.BuildConfig
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.data_struct.recipe.RecipeBasicInfo
import com.dd2d.talkingrecipe2.ui.TestingValue.TestingUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/** 현재 접속 중인 유저*/
class UserViewModel(
    private val userId: String
): ViewModel() {
    private lateinit var database: SupabaseClient
    private var _user by mutableStateOf<User>(TestingUser)
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
//            fetchUserData()
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