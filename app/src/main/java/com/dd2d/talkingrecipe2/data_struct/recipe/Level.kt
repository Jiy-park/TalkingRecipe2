package com.dd2d.talkingrecipe2.data_struct.recipe

import com.dd2d.talkingrecipe2.R

/** 레시피의 난이도
 * @property Unknown 난이도를 알 수 없음.*/
enum class Level(val description: String, val resId: Int){
    Unknown("-", R.drawable.complete_upload_recipe),
    Easy("쉬움", R.drawable.level_easy),
    Normal("보통", R.drawable.level_normal),
    Hard("어려움", R.drawable.level_hard)
}