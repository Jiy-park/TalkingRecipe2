package com.dd2d.talkingrecipe2.view_model

import android.util.Log
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.ModifyMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.OnModeError
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.ReadMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode.WriteMode
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState.OnConnected
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState.OnError
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelState.OnStable

/** [RecipeViewModel]의 모드. 현재 레시피에 대해 어떠한 작업을 진행하는지 결정.
 * * [ReadMode]
 * * [WriteMode]
 * * [ModifyMode]
 * * [OnModeError]*/
sealed class RecipeViewModelMode(val name: String){
    /** 레시피를 읽는 모드.*/
    object ReadMode: RecipeViewModelMode("Read")

    /** 레시피를 새롭게 작성하는 모드.*/
    object WriteMode: RecipeViewModelMode("Write")

    /** 레시피를 수정하는 모드.*/
    object ModifyMode: RecipeViewModelMode("Modify")

    /** 모드 선택 중 에러 발생. 직적적으로 선택해서는 안된다.*/
    object OnModeError: RecipeViewModelMode("Error")
    companion object{
        fun nameOf(modeName: String): RecipeViewModelMode {
            return when(modeName){
                "Write" -> { WriteMode }
                "Read" -> { ReadMode }
                "Modify" -> { ModifyMode }
                else -> { OnModeError }
            }
        }
    }
}


/** [RecipeViewModel]의 상태를 나타내는 값.
 * * [OnStable]
 * * [OnConnected]
 * * [OnError]*/
sealed class RecipeViewModelState{
    /** 안정적 상태. 레시피와 관련된 작업 중 어떠한 문제도 발생하지 않은 상태이다.
     * @param onEnd 레시피의 작성 단계가 모두 종료 되고, [RecipeViewModel.uploadRecipe]가 완료된 경우 true.
     * @param msg 전반적인 상태 변경에 대한 정보를 받는다. 예를 들어 [RecipeViewModelMode]변경, [RecipeViewModelState]변경 등이 있다.*/
    class OnStable(val onEnd: Boolean = false, msg: String): RecipeViewModelState(){
        init { Log.d("LOG_CHECK", "RecipeViewModelState : OnStable -> $msg") }
    }
    /** 데이터베이스와 연결된 상태. 레시피에 대한 정보를 주고 받는 상태이다.
     * @param msg 데이터 다운에 관한 정보를 받는다.*/
    class OnConnected(msg: String): RecipeViewModelState(){
        init { Log.d("LOG_CHECK", "RecipeViewModelState : OnFetching -> $msg") }
    }
    /** 에러가 발생한 상태. 데이터를 다운 받는 중 에러가 발생한 상태이다.
     * @param msg 에러에 대한 정보를 받는다.*/
    class OnError(val msg: String): RecipeViewModelState(){
        init { Log.d("LOG_CHECK", "RecipeViewModelState : OnError -> $msg") }
    }
}