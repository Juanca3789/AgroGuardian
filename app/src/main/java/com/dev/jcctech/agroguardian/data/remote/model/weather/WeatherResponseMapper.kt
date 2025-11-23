package com.dev.jcctech.agroguardian.data.remote.model.weather

import com.dev.jcctech.agroguardian.data.db.entity.WeatherEntity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun WeatherResponse.toEntityList(): List<WeatherEntity> {
    val now = Clock.System.now()

    return hourly.time.indices.mapNotNull { index ->
        val startString = hourly.time.getOrNull(index) ?: return@mapNotNull null
        val completeStartString = if (startString.length == 16) "${startString}:00Z" else startString
        val start = Instant.parse(completeStartString).toEpochMilliseconds()

        val end = hourly.time.getOrNull(index + 1)?.let {
            val completeEndString = if (it.length == 16) "${it}:00Z" else it
            Instant.parse(completeEndString).toEpochMilliseconds()
        } ?: start

        WeatherEntity(
            id = index,
            startTime = start,
            endTime = end,
            temperature = hourly.temperature.getOrNull(index) ?: 0.0,
            humidity = hourly.humidity.getOrNull(index) ?: 0,
            precipitation = hourly.precipitation.getOrNull(index) ?: 0.0,
            precipitationProbability = hourly.precipitationProbability?.getOrNull(index) ?: 0,
            condition = hourly.weatherCode?.getOrNull(index)?.let(::mapWeatherCodeToCondition) ?: "unknown",
            timestamp = now.toEpochMilliseconds()
        )
    }
}

fun mapWeatherCodeToCondition(code: Int): String = when (code) {
    in 0..1 -> "Clear"
    in 2..3 -> "Partly Cloudy"
    in 45..48 -> "Fog"
    in 51..67 -> "Drizzle"
    in 71..77 -> "Snow"
    in 80..82 -> "Rain Showers"
    in 95..99 -> "Thunderstorm"
    else -> "Unknown"
}
