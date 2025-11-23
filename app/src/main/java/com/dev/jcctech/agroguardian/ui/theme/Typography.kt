package com.dev.jcctech.agroguardian.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun agroGuardianTypography(): Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = nunitoFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = nunitoFontFamily(),
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = nunitoFontFamily(),
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = nunitoFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    titleLarge = TextStyle(
        fontFamily = montserratAlternatesFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = montserratAlternatesFamily(),
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = montserratAlternatesFamily(),
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = robotoSlapFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = robotoSlapFamily(),
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = robotoSlapFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    )
)

@Composable
fun accessibleTypography(): Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = atkinsonFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = atkinsonFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
)