package com.pineapple.app.ui.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.pineapple.app.ui.theme.PineappleTheme

@Composable
fun UserView(navController: NavController, user: String) {
    PineappleTheme {
        Text(user)
    }
}