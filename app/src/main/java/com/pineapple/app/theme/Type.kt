package com.pineapple.app.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
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

// 105, 109, 113, 120, 140

@OptIn(ExperimentalTextApi::class)
val GSF_W105 = FontFamily(
	Font(
		resId = R.font.google_sans_flex,
		variationSettings = FontVariation.Settings(FontVariation.width(105F))
	)
)
@OptIn(ExperimentalTextApi::class)
val GSF_W109 = FontFamily(
	Font(
		resId = R.font.google_sans_flex,
		variationSettings = FontVariation.Settings(FontVariation.width(109F))
	)
)
@OptIn(ExperimentalTextApi::class)
val GSF_W113 = FontFamily(
	Font(
		resId = R.font.google_sans_flex,
		variationSettings = FontVariation.Settings(FontVariation.width(113F))
	)
)
@OptIn(ExperimentalTextApi::class)
val GSF_W120 = FontFamily(
	Font(
		resId = R.font.google_sans_flex,
		variationSettings = FontVariation.Settings(FontVariation.width(120F))
	)
)
@OptIn(ExperimentalTextApi::class)
val GSF_W140 = FontFamily(
	Font(
		resId = R.font.google_sans_flex,
		variationSettings = FontVariation.Settings(FontVariation.width(140F))
	)
)

val AppTypography = Typography(
	displayLarge = TextStyle(
		fontFamily = GSF_W140,
		fontWeight = FontWeight.W500,
		fontSize = 41.sp
	),

	/* NOT USED IN NEW VERSION --
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
	// --------------------------- */

	headlineLarge = TextStyle(
		fontFamily = GSF_W113,
		fontWeight = FontWeight.W600,
		fontSize = 36.sp
	),
	headlineMedium = TextStyle(
		fontFamily = GSF_W109,
		fontWeight = FontWeight(430),
		fontSize = 28.sp
	),
	/*headlineSmall = TextStyle(
		fontFamily = Rubik,
		fontWeight = FontWeight.W400,
		fontSize = 20.sp,
		lineHeight = 25.sp,
		letterSpacing = 0.sp,
	),*/
	titleLarge = TextStyle(
		fontFamily = GSF_W109,
		fontWeight = FontWeight.W600,
		fontSize = 22.sp
	),
	titleMedium = TextStyle(
		fontFamily = GSF_W120,
		fontWeight = FontWeight.W600,
		fontSize = 16.sp
	),
	titleSmall = TextStyle(
		fontFamily = GSF_W120,
		fontWeight = FontWeight.W700,
		fontSize = 14.sp
	),
	bodyLarge = TextStyle(
		fontFamily = GSF_W109,
		fontWeight = FontWeight.W600,
		fontSize = 15.sp
	),
	bodyMedium = TextStyle(
		fontFamily = GSF_W109,
		fontWeight = FontWeight.W600,
		fontSize = 14.sp
	),
	bodySmall = TextStyle(
		fontFamily = GSF_W105,
		fontWeight = FontWeight(440),
		fontSize = 12.sp
	),
	labelLarge = TextStyle(
		fontFamily = GSF_W120,
		fontWeight = FontWeight.W700,
		fontSize = 13.sp
	),
	labelMedium = TextStyle(
		fontFamily = GSF_W109,
		fontWeight = FontWeight(550),
		fontSize = 12.sp
	),
	labelSmall = TextStyle(
		fontFamily = GSF_W109,
		fontWeight = FontWeight.W600,
		fontSize = 10.sp
	),
)
