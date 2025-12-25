package com.pineapple.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.pineapple.app.consts.MMKVKey
import com.pineapple.app.consts.NavDestinationKey
import com.pineapple.app.network.repository.RedditAuthRepository
import com.pineapple.app.network.repository.RedditRepository
import com.pineapple.app.ui.theme.PineappleTheme
import com.pineapple.app.ui.view.CommunityView
import com.pineapple.app.ui.view.HomeView
import com.pineapple.app.ui.view.KeyProviderView
import com.pineapple.app.ui.view.PostView
import com.pineapple.app.ui.view.UserView
import com.pineapple.app.ui.view.WelcomeView
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var repository: RedditAuthRepository
    @Inject lateinit var mmkv: MMKV
    lateinit var navController: NavHostController

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            navController = rememberNavController()

            PineappleTheme {
                NavHost(
                    navController = navController,
                    startDestination = if (mmkv.decodeBool(MMKVKey.ONBOARDING_COMPLETE)) {
                        NavDestinationKey.HomeView
                    } else {
                        NavDestinationKey.WelcomeView
                    },
                    enterTransition = {
                        scaleIn(initialScale = 0.9f, animationSpec = tween(350)) +
                                fadeIn(animationSpec = tween(350))
                    },
                    exitTransition = {
                        scaleOut(targetScale = 0.95f, animationSpec = tween(350)) +
                                fadeOut(animationSpec = tween(350))
                    }
                ) {
                    composable(NavDestinationKey.WelcomeView) {
                        WelcomeView(navController)
                    }
                    composable("${NavDestinationKey.KeyProviderView}/{authType}") { backStackEntry ->
                        KeyProviderView(
                            navController = navController,
                            loginType = backStackEntry.arguments?.getString("authType")!!
                        )
                    }
                    composable(NavDestinationKey.HomeView) {
                        HomeView(navController)
                    }
                    composable(
                        route = "${NavDestinationKey.HomeView}/{error}/{code}/{state}",
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "pineapple://login?error={error}&code={code}&state={state}"
                            }
                        )
                    ) {
                        mmkv.encode(MMKVKey.API_LOGIN_AUTH_CODE, it.arguments?.getString("code"))
                        mmkv.encode(MMKVKey.ONBOARDING_COMPLETE, true)
                        LaunchedEffect(Unit) {
                            repository.authenticateUser()
                        }
                        HomeView(navController)
                    }
                    composable("${NavDestinationKey.PostView}/{postID}") {
                        val postIdArg = it.arguments?.getString("postID")
                        android.util.Log.e("MainActivity", "Navigated to PostView with postID=$postIdArg")
                        PostView(
                            navController = navController,
                            postID = postIdArg!!
                        )
                    }
                    composable("${NavDestinationKey.UserView}/{user}") {
                        UserView(
                            navController = navController,
                            user = it.arguments?.getString("user")!!
                        )
                    }
                    composable("${NavDestinationKey.CommunityView}/{community}") {
                        CommunityView(
                            navController = navController,
                            community = it.arguments?.getString("community")!!
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }
}
