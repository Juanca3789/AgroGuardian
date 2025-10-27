package org.example.agroguardian.ui.viewmodel

import WeatherResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.example.agroguardian.data.db.WeatherDbSource
import org.example.agroguardian.data.db.entity.WeatherEntity
import org.example.agroguardian.data.repository.WeatherRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Duration.Companion.milliseconds

data class HourBlock(
    val startTime: String,
    val endTime: String,
    val temperature: Double,
    val humidity: Int,
    val precipitation: Double
)

data class WeatherState(
    val currentTemp: Double = 22.0,
    val condition: String = "sunny",
    val hourlyTemps: List<HourBlock> = emptyList()
)

class WeatherAndWaterViewModel(
    private val weatherDb: WeatherDbSource?,
    private val weatherRepository: WeatherRepository?,
    private val getCurrentLocation: suspend () -> Pair<Double, Double>?
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState

    init {
        weatherDb?.let { db ->
            viewModelScope.launch {
                val initial = db.getAll()
                if (initial.isNotEmpty()) {
                    _weatherState.value = mapToState(initial)
                }
            }

            viewModelScope.launch {
                db.observeAll()
                    .map { entities -> mapToState(entities) }
                    .collect { newState ->
                        if (newState.hourlyTemps.isNotEmpty()) {
                            _weatherState.value = newState
                        } else if (_weatherState.value.hourlyTemps.isNotEmpty()) {
                            _weatherState.value = _weatherState.value
                        }
                    }
            }
        }

        if (weatherRepository != null && weatherDb != null) {
            viewModelScope.launch {
                while (true) {
                    val location = getCurrentLocation()
                    if (location != null) {
                        try {
                            val (lat, lon) = location
                            val response = weatherRepository.getWeather(
                                latitude = lat,
                                longitude = lon,
                                variables = listOf("temperature_2m", "relative_humidity_2m", "precipitation", "weathercode")
                            )
                            val entities = transformResponseToEntities(response)
                            if (entities.isNotEmpty()) {
                                entities.forEach { weatherDb.insert(it) }
                            }
                        } catch (_: Exception) {
                        }
                    }

                    delayUntilNextHour()
                }
            }
        }
    }

    private fun mapToState(entities: List<WeatherEntity>): WeatherState {
        val sorted = entities.sortedByDescending { it.timestamp }
        val current = sorted.firstOrNull()
        val hourly = sorted.map {
            HourBlock(
                startTime = it.startTime,
                endTime = it.endTime,
                temperature = it.temperature,
                humidity = it.humidity.toInt(),
                precipitation = it.precipitation
            )
        }
        return WeatherState(
            currentTemp = current?.temperature ?: 22.0,
            condition = current?.condition ?: "sunny",
            hourlyTemps = hourly
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun transformResponseToEntities(response: WeatherResponse): List<WeatherEntity> {
        val timestamps = response.hourly.time
        val temps = response.hourly.temperature
        val humidity = response.hourly.humidity
        val precipitation = response.hourly.precipitation
        val codes = response.hourly.weatherCode
        val now = Clock.System.now().toEpochMilliseconds()

        return timestamps.indices.map { i ->
            val start = timestamps[i]
            val end = timestamps.getOrNull(i + 1) ?: start
            val condition = codes?.getOrNull(i)?.let { mapWeatherCode(it) } ?: "generated"
            WeatherEntity(
                id = 0,
                startTime = start,
                endTime = end,
                temperature = temps.getOrNull(i) ?: 0.0,
                humidity = humidity.getOrNull(i)?.toLong() ?: 0L,
                precipitation = precipitation.getOrNull(i) ?: 0.0,
                condition = condition,
                timestamp = now
            )
        }
    }

    private fun mapWeatherCode(code: Int): String = when (code) {
        0 -> "clear"
        1, 2, 3 -> "partly cloudy"
        45, 48 -> "fog"
        51, 53, 55 -> "drizzle"
        61, 63, 65 -> "rain"
        71, 73, 75 -> "snow"
        80, 81, 82 -> "showers"
        95 -> "thunderstorm"
        else -> "generated"
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun delayUntilNextHour() {
        val now = Clock.System.now()
        val millis = now.toEpochMilliseconds()
        val nextHourMillis = ((millis / 3600000) + 1) * 3600000
        val delayDuration = (nextHourMillis - millis).milliseconds
        delay(delayDuration)
    }
}