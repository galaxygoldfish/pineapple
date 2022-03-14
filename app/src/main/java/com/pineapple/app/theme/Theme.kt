package com.pineapple.app.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightThemeColors = lightColorScheme(
	primary = light_primary,
	onPrimary = light_onPrimary,
	primaryContainer = light_primaryContainer,
	onPrimaryContainer = light_onPrimaryContainer,
	secondary = light_secondary,
	onSecondary = light_onSecondary,
	secondaryContainer = light_secondaryContainer,
	onSecondaryContainer = light_onSecondaryContainer,
	tertiary = light_tertiary,
	onTertiary = light_onTertiary,
	tertiaryContainer = light_tertiaryContainer,
	onTertiaryContainer = light_onTertiaryContainer,
	error = light_error,
	errorContainer = light_errorContainer,
	onError = light_onError,
	onErrorContainer = light_onErrorContainer,
	background = light_background,
	onBackground = light_onBackground,
	surface = light_surface,
	onSurface = light_onSurface,
	surfaceVariant = light_surfaceVariant,
	onSurfaceVariant = light_onSurfaceVariant,
	outline = light_outline,
	inverseOnSurface = light_inverseOnSurface,
	inverseSurface = light_inverseSurface,
	inversePrimary = light_inversePrimary
) 

 private val DarkThemeColors = darkColorScheme(
	primary = dark_primary,
	onPrimary = dark_onPrimary,
	primaryContainer = dark_primaryContainer,
	onPrimaryContainer = dark_onPrimaryContainer,
	secondary = dark_secondary,
	onSecondary = dark_onSecondary,
	secondaryContainer = dark_secondaryContainer,
	onSecondaryContainer = dark_onSecondaryContainer,
	tertiary = dark_tertiary,
	onTertiary = dark_onTertiary,
	tertiaryContainer = dark_tertiaryContainer,
	onTertiaryContainer = dark_onTertiaryContainer,
	error = dark_error,
	errorContainer = dark_errorContainer,
	onError = dark_onError,
	onErrorContainer = dark_onErrorContainer,
	background = dark_background,
	onBackground = dark_onBackground,
	surface = dark_surface,
	onSurface = dark_onSurface,
	surfaceVariant = dark_surfaceVariant,
	onSurfaceVariant = dark_onSurfaceVariant,
	outline = dark_outline,
	inverseOnSurface = dark_inverseOnSurface,
	inverseSurface = dark_inverseSurface,
	inversePrimary = dark_inversePrimary
)

@Composable
fun PineappleTheme(useDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
	val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		if (!useDarkTheme) {
			dynamicLightColorScheme(LocalContext.current)
		} else {
			dynamicDarkColorScheme(LocalContext.current)
		}
	} else {
		if (!useDarkTheme) LightThemeColors else DarkThemeColors
	}
	MaterialTheme(
		colorScheme = colors,
		typography = AppTypography,
		content = content
	)
}