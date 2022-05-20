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
import com.pineapple.app.view.HomePageView
import com.pineapple.app.view.PostDetailView
import com.pineapple.app.view.SubredditView
import com.pineapple.app.view.WelcomeView

object NavDestination {
    const val WelcomeView = "welcome"
    const val HomePageView = "home"
    const val PostDetailView = "detail"
    const val SubredditView = "subreddit"
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
            composable(NavDestination.HomePageView) {
                HomePageView(navController = navigationController)
            }
            composable("${NavDestination.PostDetailView}/{subreddit}/{uid}/{link}") {
                PostDetailView(
                    navController = navigationController,
                    subreddit = it.arguments!!.getString("subreddit")!!,
                    uid = it.arguments!!.getString("uid")!!,
                    link = it.arguments!!.getString("link")!!
                )
            }
            composable("${NavDestination.SubredditView}/{subreddit}") {
                SubredditView(
                    navController = navigationController,
                    subreddit = it.arguments!!.getString("subreddit")!!
                )
            }
        }
    }

}
