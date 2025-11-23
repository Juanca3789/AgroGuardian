package com.dev.jcctech.agroguardian.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.jcctech.agroguardian.data.repository.weather.WeatherRepository
import com.dev.jcctech.agroguardian.ui.viewmodel.WeatherAndWaterViewModel

class WeatherAndWaterFactory(
    private val repository: WeatherRepository,
    private val getCurrentLocation: suspend () -> Pair<Double, Double>?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherAndWaterViewModel::class.java)) {
            return WeatherAndWaterViewModel(repository, getCurrentLocation) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}