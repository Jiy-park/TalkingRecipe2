package com.dd2d.talkingrecipe2

import android.util.Log

fun logging(msg: String = ""){
    Log.d("LOG_CHECK", msg)
}

fun Any.alog(msg: String = ""){
    Log.d("LOG_CHECK","$msg -> value : $this")
}
fun llog(msg: String): ()->Unit = {
    logging(msg)
}