package com.dev.jcctech.agroguardian.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val temperature: Double,
    val humidity: Int,
    val precipitation: Double,
    val precipitationProbability: Int,
    val condition: String,
    val timestamp: Long
)
