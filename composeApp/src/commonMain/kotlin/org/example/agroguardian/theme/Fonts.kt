package org.example.agroguardian.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import agroguardian.composeapp.generated.resources.Res
import agroguardian.composeapp.generated.resources.atkinson_hyperlegible_bold
import agroguardian.composeapp.generated.resources.atkinson_hyperlegible_regular
import agroguardian.composeapp.generated.resources.monserrat_alternates_bold
import agroguardian.composeapp.generated.resources.monserrat_alternates_light
import agroguardian.composeapp.generated.resources.monserrat_alternates_medium
import agroguardian.composeapp.generated.resources.monserrat_alternates_semibold
import agroguardian.composeapp.generated.resources.nunito_variable_wght
import agroguardian.composeapp.generated.resources.robotoslap_variable_wght
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

@Composable
fun nunitoFontFamily(): FontFamily = FontFamily(
    Font(Res.font.nunito_variable_wght, FontWeight.Light),
    Font(Res.font.nunito_variable_wght, FontWeight.Normal),
    Font(Res.font.nunito_variable_wght, FontWeight.Medium),
    Font(Res.font.nunito_variable_wght, FontWeight.SemiBold),
    Font(Res.font.nunito_variable_wght, FontWeight.Bold)
)


@Composable
fun robotoSlapFamily(): FontFamily = FontFamily(
    Font(Res.font.robotoslap_variable_wght, FontWeight.Light),
    Font(Res.font.robotoslap_variable_wght, FontWeight.Normal),
    Font(Res.font.robotoslap_variable_wght, FontWeight.Medium),
    Font(Res.font.robotoslap_variable_wght, FontWeight.SemiBold),
    Font(Res.font.robotoslap_variable_wght, FontWeight.Bold)
)

@Composable
fun montserratAlternatesFamily(): FontFamily = FontFamily(
    Font(Res.font.monserrat_alternates_light, FontWeight.Normal),
    Font(Res.font.monserrat_alternates_medium, FontWeight.Medium),
    Font(Res.font.monserrat_alternates_semibold, FontWeight.SemiBold),
    Font(Res.font.monserrat_alternates_bold, FontWeight.Bold)
)

@Composable
fun atkinsonFamily(): FontFamily = FontFamily(
    Font(Res.font.atkinson_hyperlegible_regular, FontWeight.Normal),
    Font(Res.font.atkinson_hyperlegible_bold, FontWeight.Bold)
)