package com.pineapple.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.pineapple.app.R

val GoogleSans = FontFamily(
    Font(R.font.google_sans_regular, weight = FontWeight.Normal),
    Font(R.font.google_sans_medium, weight = FontWeight.Medium),
    Font(R.font.google_sans_semibold, weight = FontWeight.SemiBold),
    Font(R.font.google_sans_bold, weight = FontWeight.Bold)
)

val PineappleTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = GoogleSans),
    displayMedium = Typography().displayMedium.copy(fontFamily = GoogleSans),
    displaySmall = Typography().displaySmall.copy(fontFamily = GoogleSans),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = GoogleSans),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = GoogleSans),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = GoogleSans),
    titleLarge = Typography().titleLarge.copy(fontFamily = GoogleSans),
    titleMedium = Typography().titleMedium.copy(fontFamily = GoogleSans),
    titleSmall = Typography().titleSmall.copy(fontFamily = GoogleSans),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = GoogleSans),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = GoogleSans),
    bodySmall = Typography().bodySmall.copy(fontFamily = GoogleSans),
    labelLarge = Typography().labelLarge.copy(fontFamily = GoogleSans),
    labelMedium = Typography().labelMedium.copy(fontFamily = GoogleSans),
    labelSmall = Typography().labelSmall.copy(fontFamily = GoogleSans)
)