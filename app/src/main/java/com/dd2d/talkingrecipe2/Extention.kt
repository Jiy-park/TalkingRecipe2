package com.dd2d.talkingrecipe2

/** Supabase Storage로부터 받은 결과물을 사용가능한 url로 변환.*/
fun String.toSupabaseUrl() = "${BuildConfig.SUPABASE_URL}/storage/v1/$this"