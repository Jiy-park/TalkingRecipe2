package com.dd2d.talkingrecipe2.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dd2d.talkingrecipe2.alog
import com.dd2d.talkingrecipe2.data_struct.User
import com.dd2d.talkingrecipe2.data_struct.UserDTO
import com.dd2d.talkingrecipe2.data_struct.sampleUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//class UserModel(userId: Int){
//    fun getUserByUserId() : Flow<User>{
//
//    }
//}

/** 현재 접속 중인 유저*/
class UserViewModel(
    val supabase: SupabaseClient,
    val userId: Int
): ViewModel() {
    private var _user by mutableStateOf<User>(sampleUser)
    val user: User
        get() = _user

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchData()
        }
    }

    private suspend fun fetchData(){
        _user = supabase
            .from("user")
            .select {
                filter {
                    eq("userId", userId)
                }
            }.decodeSingle()
    }

}