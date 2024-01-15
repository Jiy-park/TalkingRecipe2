package com.dd2d.talkingrecipe2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dd2d.talkingrecipe2.navigation.AppNavigation
import com.dd2d.talkingrecipe2.navigation.Screen
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination
import com.dd2d.talkingrecipe2.navigation.subScreenGraph
import com.dd2d.talkingrecipe2.ui.theme.TalkingRecipe2Theme
import com.dd2d.talkingrecipe2.view.main_screen.MainScreen
import com.dd2d.talkingrecipe2.view.sub_screen.SubScreen
import com.dd2d.talkingrecipe2.view_model.UserViewModel
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

private val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY
) {
    install(Postgrest)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        val userViewModel = UserViewModel(supabase = supabase, userId = 1_000_001)
        super.onCreate(savedInstanceState)
        setContent {
            TalkingRecipe2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun App(modifier: Modifier = Modifier){
    AppNavigation(modifier = modifier)
}