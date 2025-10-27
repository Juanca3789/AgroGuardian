package org.example.agroguardian.data.db

import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.example.agroguardian.data.db.entity.WeatherEntity
import org.example.agroguardian.data.db.entity.toEntity

class WeatherDbSource(
    db: AppDatabase,
) {
    private val queries = db.dbQueries

    fun observeAll(): Flow<List<WeatherEntity>> {
        return queries.selectAll()
            .asFlow()
            .map { it.executeAsList().map { weather -> weather.toEntity() } }
            .flowOn(Dispatchers.IO)
    }

    suspend fun insert(entity: WeatherEntity) {
        withContext(Dispatchers.IO) {
            queries.insertWeather(
                startTime = entity.startTime,
                endTime = entity.endTime,
                temperature = entity.temperature,
                humidity = entity.humidity,
                precipitation = entity.precipitation,
                condition = entity.condition,
                timestamp = entity.timestamp
            )
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}