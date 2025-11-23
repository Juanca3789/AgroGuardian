package com.dev.jcctech.agroguardian.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev.jcctech.agroguardian.data.db.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather ORDER BY startTime ASC")
    fun getAllWeather(): Flow<List<WeatherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(weatherEntityList: List<WeatherEntity>)

    @Query("DELETE FROM weather")
    suspend fun clear()
}