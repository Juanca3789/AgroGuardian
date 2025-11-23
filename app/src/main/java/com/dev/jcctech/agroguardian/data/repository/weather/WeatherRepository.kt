package com.dev.jcctech.agroguardian.data.repository.weather

import com.dev.jcctech.agroguardian.data.db.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun observeAllWeather(): Flow<List<WeatherEntity>>
    suspend fun syncWeather(latitude: Double, longitude: Double)
}