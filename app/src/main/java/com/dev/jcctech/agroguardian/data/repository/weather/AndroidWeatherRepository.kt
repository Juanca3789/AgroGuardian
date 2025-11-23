package com.dev.jcctech.agroguardian.data.repository.weather

import android.util.Log
import com.dev.jcctech.agroguardian.data.db.dao.WeatherDao
import com.dev.jcctech.agroguardian.data.db.entity.WeatherEntity
import com.dev.jcctech.agroguardian.data.remote.model.weather.toEntityList
import com.dev.jcctech.agroguardian.data.remote.service.WeatherApiService
import kotlinx.coroutines.flow.Flow

class AndroidWeatherRepository(
    private val api: WeatherApiService,
    private val dao: WeatherDao
) : WeatherRepository {

    override fun observeAllWeather(): Flow<List<WeatherEntity>> = dao.getAllWeather()

    override suspend fun syncWeather(
        latitude: Double,
        longitude: Double,
    ) {
        try {
            val remote = api.getWeather(
                latitude = latitude,
                longitude = longitude,
                hourly = listOf("temperature_2m", "relative_humidity_2m", "precipitation", "weathercode", "precipitation_probability").joinToString(","),
                timezone = "auto"
            )
            val entities = remote.toEntityList()
            dao.insertAll(entities)
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Error syncing weather data (Ask Gemini)", e)
        }
    }
}
