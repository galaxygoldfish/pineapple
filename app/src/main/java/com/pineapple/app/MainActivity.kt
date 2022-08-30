package com.pineapple.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Down
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Right
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Up
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.getPreferences
import com.pineapple.app.view.*

object NavDestination {
    const val WelcomeView = "welcome"
    const val HomePageView = "home"
    const val PostDetailView = "detail"
    const val SubredditView = "subreddit"
    const val MediaDetailView = "media"
    const val SettingsView = "settings"
    const val UserView = "user"
}

class MainActivity : ComponentActivity() {

    lateinit var navigationController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
        val onboardingComplete = getPreferences().getBoolean("ONBOARDING_COMPLETE", false)
        AnimatedNavHost(
            navController = navigationController,
            startDestination = if (onboardingComplete) {
                NavDestination.HomePageView
            } else {
                NavDestination.WelcomeView
            }
        ) {
            composable(NavDestination.WelcomeView) {
                WelcomeView(navController = navigationController)
            }
            composable(NavDestination.HomePageView) {
                HomePageView(navController = navigationController)
            }
            composable(
                route = "${NavDestination.PostDetailView}/{subreddit}/{uid}/{link}",
                enterTransition = { slideIntoContainer(towards = Up) },
                exitTransition = { slideOutOfContainer(towards = Down) }
            ) {
                PostDetailView(
                    navController = navigationController,
                    subreddit = it.arguments!!.getString("subreddit")!!,
                    uid = it.arguments!!.getString("uid")!!,
                    link = it.arguments!!.getString("link")!!
                )
            }
            composable(
                route = "${NavDestination.SubredditView}/{subreddit}",
                enterTransition = { slideIntoContainer(towards = Up) },
                exitTransition = { slideOutOfContainer(towards = Down) }
            ) {
                SubredditView(
                    navController = navigationController,
                    subreddit = it.arguments!!.getString("subreddit")!!
                )
            }
            composable(
                route = "${NavDestination.MediaDetailView}/{mediaType}/{encodedUrl}/{domain}/{title}",
                enterTransition = { expandIn() },
                exitTransition = { slideOutOfContainer(towards = Down) }
            ) {
                MediaDetailView(
                    navController = navigationController,
                    mediaType = it.arguments!!.getString("mediaType")!!,
                    encodedUrl = it.arguments!!.getString("encodedUrl")!!,
                    domain = it.arguments!!.getString("domain")!!,
                    titleText = it.arguments!!.getString("title")!!
                )
            }
            composable(
                route = NavDestination.SettingsView,
                enterTransition = { slideIntoContainer(towards = Left) },
                exitTransition = { slideOutOfContainer(towards = Right) }
            ) {
                SettingsView(navController = navigationController)
            }
            composable(
                route = "${NavDestination.UserView}/{user}",
                enterTransition = { slideIntoContainer(towards = Up) },
                exitTransition = { slideOutOfContainer(towards = Down) }
            ) {
                UserView(
                    navController = navigationController,
                    user = it.arguments!!.getString("user")!!
                )
            }
        }
    }
}
