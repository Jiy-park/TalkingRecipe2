package com.dd2d.talkingrecipe2.data_struct.recipe_create

import android.util.Log
import com.dd2d.talkingrecipe2.view_model.CreateViewModel

/**
 *- [Init] - [CreateViewModel]의 초기화.
 *- [OnFetching] - 레시피 관련 정보를 다운 중.
 *- [Stable] - 특별한 상태가 없음.
 *- [OnUploading] - 레시필를 데이터베이스에 업로드 함.
 *- [OnError] - 레시피 다운 중 에러 발생*/
sealed class CreateState {
    object Init: CreateState(){
        init { Log.d("LOG_CHECK", "Create State : Init -> initial view model") }
    }
    class OnFetching(msg: String): CreateState(){
        init { Log.d("LOG_CHECK", "Create State : OnFetching -> $msg") }
    }
    class Stable(msg: String): CreateState(){
        init { Log.d("LOG_CHECK", "Create State : Stable -> $msg") }
    }
    class OnError(val msg: String): CreateState(){
        init { Log.e("LOG_CHECK", "Create State : OnError -> $msg") }
    }
    class OnUploading(msg: String): CreateState(){
        init { Log.d("LOG_CHECK", "Create State : OnUploading -> $msg") }
    }
}