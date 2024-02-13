package com.dd2d.talkingrecipe2.navigation

/** 앱 내의 화면. navigation을 통해 각 화면을 이동한다.
 * @property Login
 * @property Main
 * @property Sub
 * @property RecipeWrite
 * @property Search
 * @property RecipeRead*/
enum class Screen(val route: String){
    /** 로그인 화면. 유저는 해당 화면에서 회원가입을 진행할 수 있음.*/
    Login("log in screen"),

    /** 로그인 후 유저가 볼 첫 화면. 해당 화면으로부터 각 화면으로 이동 가능.*/
    Main("main screen"),

    /** 유저의 정보를 볼 수 있는 화면. 화면 내에서 작성글, 친구목록, 저장한 레시피 등을 볼 수 있음.
     * 유저의 프로필, 배경 이미지, 이름을 변경 가능한 화면.*/
    Sub("sub screen"),

    /** 레시피를 작성하는 화면. 해당 화면에서 레시피 작성 또는 수정이 가능함.*/
    RecipeWrite("recipe write screen"),

    /** 레시피 또는 유저를 검색하는 화면. 추가적으로 유저도 검색 가능. */
    Search("recipe search screen"),

    /** 레시피를 읽는 화면.*/
    RecipeRead("recipe read screen"),
}

