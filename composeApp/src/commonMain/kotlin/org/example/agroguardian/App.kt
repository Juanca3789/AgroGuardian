package org.example.agroguardian

import androidx.compose.runtime.*
import org.example.agroguardian.data.db.AppDatabase
import org.example.agroguardian.data.db.WeatherDbSource
import org.example.agroguardian.data.inyection.AppProviders
import org.example.agroguardian.data.repository.WeatherRepository

import org.example.agroguardian.theme.AgroGuardianTheme
import org.example.agroguardian.ui.Main

@Composable
fun App(
    appDatabase: AppDatabase?,
    weatherRepository: WeatherRepository?,
    getCurrentLocation: suspend () -> Pair<Double, Double>?
) {
    CompositionLocalProvider(
        AppProviders.LocalWeatherRepository provides WeatherDbSource(appDatabase!!),
        AppProviders.RemoteWeatherRepository provides weatherRepository,
                AppProviders.LocationProvider provides getCurrentLocation
    ) {
        AgroGuardianTheme {
            Main()
        }
    }
}