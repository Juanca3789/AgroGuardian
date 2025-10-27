package org.example.agroguardian.data.inyection

import androidx.compose.runtime.staticCompositionLocalOf
import org.example.agroguardian.data.db.WeatherDbSource
import org.example.agroguardian.data.repository.WeatherRepository

object AppProviders {
    val RemoteWeatherRepository = staticCompositionLocalOf<WeatherRepository?> {
        error("WeatherRepository no ha sido inyectado.")
    }

    val LocalWeatherRepository = staticCompositionLocalOf<WeatherDbSource?> {
        error("WeatherDbSource no ha sido inyectado.")
    }

    val LocationProvider = staticCompositionLocalOf<suspend () -> Pair<Double, Double>?> {
        error("getCurrentLocation no ha sido inyectado.")
    }
}
