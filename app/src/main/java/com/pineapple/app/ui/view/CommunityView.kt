package com.pineapple.app.ui.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.pineapple.app.ui.theme.PineappleTheme

@Composable
fun CommunityView(navController: NavController, community: String) {
    PineappleTheme {
        Text(community)
    }
}