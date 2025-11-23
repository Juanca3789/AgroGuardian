package com.dev.jcctech.agroguardian.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import com.dev.jcctech.agroguardian.R

@Composable
fun nunitoFontFamily(): FontFamily = FontFamily(
    Font(R.font.nunito_variable_wght, FontWeight.Light),
    Font(R.font.nunito_variable_wght, FontWeight.Normal),
    Font(R.font.nunito_variable_wght, FontWeight.Medium),
    Font(R.font.nunito_variable_wght, FontWeight.SemiBold),
    Font(R.font.nunito_variable_wght, FontWeight.Bold)
)


@Composable
fun robotoSlapFamily(): FontFamily = FontFamily(
    Font(R.font.robotoslap_variable_wght, FontWeight.Light),
    Font(R.font.robotoslap_variable_wght, FontWeight.Normal),
    Font(R.font.robotoslap_variable_wght, FontWeight.Medium),
    Font(R.font.robotoslap_variable_wght, FontWeight.SemiBold),
    Font(R.font.robotoslap_variable_wght, FontWeight.Bold)
)

@Composable
fun montserratAlternatesFamily(): FontFamily = FontFamily(
    Font(R.font.monserrat_alternates_light, FontWeight.Normal),
    Font(R.font.monserrat_alternates_medium, FontWeight.Medium),
    Font(R.font.monserrat_alternates_semibold, FontWeight.SemiBold),
    Font(R.font.monserrat_alternates_bold, FontWeight.Bold)
)

@Composable
fun atkinsonFamily(): FontFamily = FontFamily(
    Font(R.font.atkinson_hyperlegible_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold)
)