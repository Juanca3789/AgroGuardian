package com.dev.jcctech.agroguardian.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev.jcctech.agroguardian.data.db.entity.WeatherEntity
import com.dev.jcctech.agroguardian.data.inyection.AppProviders
import com.dev.jcctech.agroguardian.ui.viewmodel.WeatherAndWaterViewModel
import com.dev.jcctech.agroguardian.ui.viewmodel.factory.WeatherAndWaterFactory
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@Composable
fun WeatherAndWaterScreen() {
    val repository = AppProviders.LocalWeatherRepository.current
    val getCurrentLocation = AppProviders.LocalLocationProvider.current

    val factory = remember(repository, getCurrentLocation) {
        WeatherAndWaterFactory(repository, getCurrentLocation)
    }

    val viewModel: WeatherAndWaterViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        Log.d("WeatherScreen", "WeatherAndWaterScreen en composición")
    }

    val currentTemp by viewModel.currentTemp.collectAsState()
    val condition by viewModel.condition.collectAsState()
    val weatherEntities by viewModel.weatherEntities.collectAsState()

    WeatherAndWaterInterface(
        currentTemp = currentTemp ?: 0.0,
        condition = condition ?: "desconocido",
        weatherData = weatherEntities
    )
}

@Composable
fun WeatherAndWaterInterface(
    currentTemp: Double,
    condition: String,
    weatherData: List<WeatherEntity>
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp,
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = innerPadding.calculateBottomPadding()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (weatherData.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    WeatherSummaryCard(currentTemp, condition)
                }

                Box(modifier = Modifier.weight(2f)) {
                    GroupedHourlyForecast(weatherData)
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun WeatherSummaryCard(temp: Double, condition: String) {
    val colorScheme = MaterialTheme.colorScheme
    val (icon, tint) = when (condition.lowercase()) {
        "clear", "sunny" -> {
            val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
            if (hour in 6..18) { // Daytime from 6 AM to 6:59 PM
                Icons.Default.WbSunny to Color(0xFFFFC107)
            } else { // Nighttime
                Icons.Default.Nightlight to Color(0xFF90A4AE)
            }
        }
        "partly cloudy", "cloudy" -> Icons.Default.Cloud to Color(0xFF90A4AE)
        "fog" -> Icons.Default.VisibilityOff to Color(0xFFB0BEC5)
        "drizzle", "rain", "rain showers" -> Icons.Default.Umbrella to Color(0xFF2196F3)
        "snow" -> Icons.Default.AcUnit to Color(0xFF80DEEA)
        "thunderstorm" -> Icons.Default.Bolt to Color(0xFFFF5722)
        else -> Icons.Default.Cloud to colorScheme.outline
    }

    val translated = when (condition.lowercase()) {
        "clear", "sunny" -> "Despejado"
        "partly cloudy", "cloudy" -> "Nublado"
        "fog" -> "Niebla"
        "drizzle", "rain", "rain showers" -> "Lluvia"
        "snow" -> "Nieve"
        "thunderstorm" -> "Tormenta"
        else -> "Condición desconocida"
    }

    val formattedTime = formatTime(Clock.System.now().toEpochMilliseconds())

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = condition,
                tint = tint,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${temp.roundTo2Decimals()}°C",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = translated,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Actualizado: $formattedTime",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun GroupedHourlyForecast(blocks: List<WeatherEntity>) {
    val grouped = blocks.groupBy {
        Instant.fromEpochMilliseconds(it.startTime)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        grouped.forEach { (dateKey, group) ->
            item {
                var expanded by remember { mutableStateOf(false) }

                val avgTemp = group.map { it.temperature }.average().roundTo2Decimals()
                val avgHumidity = group.map { it.humidity }.average().roundToInt()
                val avgPrecipProbability = group.map { it.precipitationProbability }.average().roundToInt()

                Card(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = formatDate(dateKey.toString()),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Promedio: ${avgTemp}°C, ${avgHumidity}% humedad, ${avgPrecipProbability}% prob. lluvia",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = if (expanded) "Contraer" else "Expandir",
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(if (expanded) 180f else 0f),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(visible = expanded) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                group.forEach {
                                    TemperatureBlock(it)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TemperatureBlock(block: WeatherEntity) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${formatTime(block.startTime)} → ${formatTime(block.endTime)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Umbrella,
                        contentDescription = "Probabilidad de Precipitación",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${block.precipitationProbability}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.Opacity,
                        contentDescription = "Humedad",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${block.humidity}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Text(
                text = "${block.temperature.roundTo2Decimals()}°C",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun Double.roundTo2Decimals(): Double {
    return (this * 100).roundToInt() / 100.0
}

@OptIn(ExperimentalTime::class)
private fun formatTime(epochMillis: Long): String {
    val instant = Instant.fromEpochMilliseconds(epochMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

fun formatDate(isoDate: String): String {
    val parts = isoDate.split("-")
    if (parts.size != 3) return isoDate // Fallback

    val (year, month, day) = parts.map { it.toInt() }

    val dayOfWeek = LocalDate(year, month, day).dayOfWeek
    val dayName = when (dayOfWeek) {
        DayOfWeek.MONDAY -> "Lunes"
        DayOfWeek.TUESDAY -> "Martes"
        DayOfWeek.WEDNESDAY -> "Miércoles"
        DayOfWeek.THURSDAY -> "Jueves"
        DayOfWeek.FRIDAY -> "Viernes"
        DayOfWeek.SATURDAY -> "Sábado"
        else -> "Domingo"
    }

    val monthName = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )[month - 1]

    return "$dayName $day de $monthName"
}
