package com.dev.jcctech.agroguardian.data.remote.service

import com.dev.jcctech.agroguardian.data.remote.model.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}