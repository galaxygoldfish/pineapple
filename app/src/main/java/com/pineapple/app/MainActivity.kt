package com.pineapple.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.view.WelcomeView

object NavDestination {
    const val WelcomeView = "welcome"
}

class MainActivity : ComponentActivity() {

    lateinit var navigationController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PineappleTheme {
                NavigationHost()
            }
        }
    }


    @Composable
    @OptIn(ExperimentalAnimationApi::class)
    fun NavigationHost() {
        navigationController = rememberAnimatedNavController()
        NavHost(
            navController = navigationController,
            startDestination = NavDestination.WelcomeView
        ) {
            composable(NavDestination.WelcomeView) {
                WelcomeView(navController = navigationController)
            }
        }
    }

}
