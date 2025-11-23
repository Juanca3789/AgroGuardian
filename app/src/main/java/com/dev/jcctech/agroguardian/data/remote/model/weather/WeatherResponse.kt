package com.dev.jcctech.agroguardian.data.remote.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    @SerialName("generationtime_ms") val generationTimeMs: Double,
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int,
    val timezone: String,
    @SerialName("timezone_abbreviation") val timezoneAbbreviation: String,
    val elevation: Double,
    @SerialName("hourly_units") val hourlyUnits: WeatherUnits,
    val hourly: WeatherHourlyData
)