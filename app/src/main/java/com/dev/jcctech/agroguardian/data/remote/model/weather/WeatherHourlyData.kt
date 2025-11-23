package com.dev.jcctech.agroguardian.data.remote.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherHourlyData(
    val time: List<String>,
    @SerialName("temperature_2m") val temperature: List<Double>,
    @SerialName("relative_humidity_2m") val humidity: List<Int>,
    val precipitation: List<Double>,
    @SerialName("precipitation_probability") val precipitationProbability: List<Int>? = null,
    @SerialName("weathercode") val weatherCode: List<Int>? = null
)
