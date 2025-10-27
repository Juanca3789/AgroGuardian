package org.example.agroguardian.data.repository

import WeatherResponse

interface WeatherRepository {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        variables: List<String> = listOf("temperature_2m", "relative_humidity_2m", "precipitation")
    ): WeatherResponse
}