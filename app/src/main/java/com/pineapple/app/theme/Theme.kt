package com.pineapple.app.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pineapple.app.util.getPreferences


@Composable
fun PineappleTheme(useDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		if (!useDarkTheme) {
			dynamicLightColorScheme(LocalContext.current)
		} else {
			dynamicDarkColorScheme(LocalContext.current)
		}
	} else {
		LocalContext.current.getPreferences().getString("APP_THEME", "THEME_DEEP_SEA")?.let { key ->
			themeOptionMap()[key]?.let { theme ->
				if (!useDarkTheme) theme.first else theme.second
			}
		}
	}
	MaterialTheme(
		colorScheme = colors!!,
		typography = AppTypography,
		content = content
	)
}