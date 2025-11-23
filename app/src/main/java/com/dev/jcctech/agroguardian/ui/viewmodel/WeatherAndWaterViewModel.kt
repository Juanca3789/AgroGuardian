package com.dev.jcctech.agroguardian.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.jcctech.agroguardian.data.db.entity.WeatherEntity
import com.dev.jcctech.agroguardian.data.repository.weather.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherAndWaterViewModel(
    private val repository: WeatherRepository,
    private val getCurrentLocation: suspend () -> Pair<Double, Double>?
) : ViewModel() {

    private val _currentTemp = MutableStateFlow<Double?>(null)
    val currentTemp: StateFlow<Double?> = _currentTemp

    private val _condition = MutableStateFlow<String?>(null)
    val condition: StateFlow<String?> = _condition

    private val _weatherEntities = MutableStateFlow<List<WeatherEntity>>(emptyList())
    val weatherEntities: StateFlow<List<WeatherEntity>> = _weatherEntities

    init {
        Log.d("WeatherVM", "init: lanzando observación y sincronización")
        observeLocalWeather()
        syncWeatherPeriodically()
    }

    private fun observeLocalWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("WeatherVM", "Observando datos locales")
            repository.observeAllWeather().collect { entities ->
                Log.d("WeatherVM", "Recibidos ${entities.size} registros desde la DB")
                val sorted = entities.sortedByDescending { it.timestamp }
                val current = sorted.firstOrNull()
                Log.d("WeatherVM", "Registro más reciente: $current")
                _currentTemp.value = current?.temperature
                _condition.value = current?.condition
                _weatherEntities.value = sorted
            }
        }
    }

    private fun syncWeatherPeriodically() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("WeatherVM", "Iniciando sincronización periódica")
            while (true) {
                try {
                    val location = getCurrentLocation()
                    if (location == null) {
                        Log.e("WeatherVM", "Ubicación nula, reintentando en 10s")
                        delay(10_000)
                        continue
                    }
                    val (lat, lon) = location
                    Log.d("WeatherVM", "Ubicación obtenida: lat=$lat, lon=$lon")
                    repository.syncWeather(lat, lon)
                    Log.d("WeatherVM", "Sincronización completada")
                } catch (e: Exception) {
                    Log.e("WeatherVM", "Error en sincronización", e)
                }
                delayUntilNextHour()
            }
        }
    }

    private suspend fun delayUntilNextHour() {
        val now = System.currentTimeMillis()
        val nextHourMillis = ((now / 3600000) + 1) * 3600000
        val delayMillis = nextHourMillis - now
        Log.d("WeatherVM", "Esperando ${delayMillis / 1000}s hasta la próxima hora")
        delay(delayMillis)
    }
}