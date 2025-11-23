package com.dev.jcctech.agroguardian.data.remote.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherUnits(
    val time: String,
    @SerialName("temperature_2m") val temperature: String,
    @SerialName("relative_humidity_2m") val humidity: String,
    val precipitation: String
)