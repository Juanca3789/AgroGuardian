package org.example.agroguardian.data.db.entity

import org.example.agroguardian.data.db.Weather

data class WeatherEntity(
    val id: Long,
    val startTime: String,
    val endTime: String,
    val temperature: Double,
    val humidity: Long,
    val precipitation: Double,
    val condition: String,
    val timestamp: Long
)

fun Weather.toEntity() = WeatherEntity(
    id = id,
    startTime = startTime,
    endTime = endTime,
    temperature = temperature,
    humidity = humidity,
    precipitation = precipitation,
    condition = condition,
    timestamp = timestamp
)