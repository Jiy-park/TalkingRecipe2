package com.dd2d.talkingrecipe2.navigation

import com.dd2d.talkingrecipe2.navigation.Screen.Login
import com.dd2d.talkingrecipe2.navigation.Screen.Main
import com.dd2d.talkingrecipe2.navigation.Screen.Recipe
import com.dd2d.talkingrecipe2.navigation.Screen.Search
import com.dd2d.talkingrecipe2.navigation.Screen.Sub
import com.dd2d.talkingrecipe2.view.recipe_read_screen.RecipeReadScreen
import com.dd2d.talkingrecipe2.view.recipe_write_screen.RecipeWriteScreen
import com.dd2d.talkingrecipe2.view_model.RecipeViewModelMode

/** 앱 내의 화면. navigation을 통해 각 화면을 이동한다.
 * @property Login
 * @property Main
 * @property Sub
 * @property Recipe
 * @property Search*/
enum class Screen(val route: String){
    /** 로그인 화면. 유저는 해당 화면에서 회원가입을 진행할 수 있음.
     * * 접근 방법 : "${Screen.Login.route}"*/
    Login("log in screen"),

    /** 로그인 후 유저가 볼 첫 화면. 해당 화면으로부터 각 화면으로 이동 가능.
     **접근 방법 : "${Screen.Main.route}"*/
    Main("main screen"),

    /** 유저의 정보를 볼 수 있는 화면. 화면 내에서 작성글, 친구목록, 저장한 레시피 등을 볼 수 있음.
     * 화면의 시작점에는 다음이 있으며 [SubScreenDestination]을 통해 선택할 수 있다.
     * 1. 작성글 - [SubScreenDestination.MyPost]
     * 2. 친구 목록 - [SubScreenDestination.Friend]
     * 3. 보관함 - [SubScreenDestination.SavePost]
     * * 접근 방법 : "${Screen.Sub.route}/{destination}"*/
    Sub("sub screen"),

    /** 레시피 또는 유저를 검색하는 화면. 추가적으로 유저도 검색 가능.
     * route = TODO("라우트 확정되면 적어둘것.")*/
    Search("recipe search screen"),

    /** 레시피에 대한 작업을 하는 화면.
     * 레시피에 대한 작업으로 다음이 있으며 [RecipeViewModelMode]를 통해 세부 작업을 선택할 수 있다.
     * 1. 레시피 읽기 - [RecipeReadScreen]
     * 2. 레시피 쓰기 - [RecipeWriteScreen]
     * 3. 레시피 수정하기 - [RecipeWriteScreen]
     * * 접근 방법 : "${Screen.Recipe.route}/{recipeViewModelMode.name}"*/
    Recipe("recipe screen"),
}

