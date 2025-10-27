package org.example.agroguardian.data.repository

import WeatherResponse
import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AndroidWeatherRepository : WeatherRepository {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        variables: List<String>
    ): WeatherResponse {
        return client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("hourly", variables.joinToString(","))
            parameter("timezone", "auto")
        }.body()
    }
}