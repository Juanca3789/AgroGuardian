package com.dev.jcctech.agroguardian

import androidx.compose.runtime.*
import com.dev.jcctech.agroguardian.data.repository.forum.ForumRepository
import com.dev.jcctech.agroguardian.ui.theme.AgroGuardianTheme
import com.dev.jcctech.agroguardian.data.inyection.AppProviders
import com.dev.jcctech.agroguardian.data.repository.weather.WeatherRepository
import com.dev.jcctech.agroguardian.ui.Main

@Composable
fun App(
    weatherRepository: WeatherRepository,
    forumRepository: ForumRepository,
    getCurrentLocation: suspend () -> Pair<Double, Double>?
) {

    CompositionLocalProvider(
        AppProviders.LocalWeatherRepository provides weatherRepository,
        AppProviders.LocalForumRepository provides forumRepository,
        AppProviders.LocalLocationProvider provides getCurrentLocation,
    ) {
        AgroGuardianTheme {
            Main()
        }
    }
}