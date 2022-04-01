package com.pineapple.app.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pineapple.app.R

val Rubik = FontFamily(
	Font(resId = R.font.rubik_bold, weight = FontWeight.Bold),
	Font(resId = R.font.rubik_semibold, weight = FontWeight.SemiBold),
	Font(resId = R.font.rubik_medium, weight = FontWeight.Medium),
	Font(resId = R.font.rubik_regular, weight = FontWeight.Normal),
	Font(resId = R.font.rubik_light, weight = FontWeight.Thin)
)

val AppTypography = Typography(
	displayLarge = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 57.sp,
		lineHeight = 64.sp,
		letterSpacing = (-0.25).sp,
	),
	displayMedium = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 45.sp,
		lineHeight = 52.sp,
		letterSpacing = 0.sp,
	),
	displaySmall = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W500,
		fontSize = 25.sp,
		lineHeight = 28.sp,
		letterSpacing = 0.sp,
	),
	headlineLarge = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 32.sp,
		lineHeight = 40.sp,
		letterSpacing = 0.sp,
	),
	headlineMedium = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 28.sp,
		lineHeight = 36.sp,
		letterSpacing = 0.sp,
	),
	headlineSmall = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 20.sp,
		lineHeight = 25.sp,
		letterSpacing = 0.sp,
	),
	titleLarge = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 22.sp,
		lineHeight = 25.sp,
		letterSpacing = 0.sp,
	),
	titleMedium = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 18.sp,
		lineHeight = 28.sp,
		letterSpacing = 0.sp,
	),
	titleSmall = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.Medium,
		fontSize = 14.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.1.sp,
	),
	labelLarge = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.Medium,
		fontSize = 14.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.1.sp,
	),
	bodyLarge = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 16.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.5.sp,
	),
	bodyMedium = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 15.sp,
		lineHeight = 18.sp,
		letterSpacing = 0.25.sp,
	),
	bodySmall = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 12.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.4.sp,
	),
	labelMedium = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.Medium,
		fontSize = 12.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.5.sp,
	),
	labelSmall = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.Medium,
		fontSize = 12.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.5.sp,
	),
)
