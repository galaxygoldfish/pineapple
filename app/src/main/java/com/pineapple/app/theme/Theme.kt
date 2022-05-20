package com.pineapple.app.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext



@Composable
fun PineappleTheme(useDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		if (!useDarkTheme) {
			dynamicLightColorScheme(LocalContext.current)
		} else {
			dynamicDarkColorScheme(LocalContext.current)
		}
	} else {
		if (!useDarkTheme) PineappleLight else PineappleDark
	}
	MaterialTheme(
		colorScheme = colors,
		typography = AppTypography,
		content = content
	)
}