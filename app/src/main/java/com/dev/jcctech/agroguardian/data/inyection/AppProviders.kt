package com.dev.jcctech.agroguardian.data.inyection

import androidx.compose.runtime.staticCompositionLocalOf
import com.dev.jcctech.agroguardian.data.repository.forum.ForumRepository
import com.dev.jcctech.agroguardian.data.repository.weather.WeatherRepository

object AppProviders {

    val LocalWeatherRepository = staticCompositionLocalOf<WeatherRepository> {
        error("LocalWeatherRepository no ha sido proporcionado.")
    }

    val LocalForumRepository = staticCompositionLocalOf<ForumRepository> {
        error("LocalForumRepository no ha sido proporcionado.")
    }

    val LocalLocationProvider = staticCompositionLocalOf<suspend () -> Pair<Double, Double>?> {
        error("LocalLocationProvider no ha sido proporcionado.")
    }
}