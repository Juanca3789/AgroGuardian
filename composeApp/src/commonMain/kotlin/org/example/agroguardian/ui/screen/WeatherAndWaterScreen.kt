package org.example.agroguardian.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.example.agroguardian.data.inyection.AppProviders
import org.example.agroguardian.ui.viewmodel.HourBlock
import org.example.agroguardian.ui.viewmodel.WeatherAndWaterViewModel
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DayOfWeek


@Composable
fun WeatherAndWaterScreen() {
    val weatherDbSource = AppProviders.LocalWeatherRepository.current
    val weatherRepository = AppProviders.RemoteWeatherRepository.current
    val getCurrentLocation = AppProviders.LocationProvider.current

    val viewModel = remember {
        WeatherAndWaterViewModel(
            weatherDb = weatherDbSource,
            weatherRepository = weatherRepository,
            getCurrentLocation = getCurrentLocation
        )
    }
    val state by viewModel.weatherState.collectAsState()
    WeatherAndWaterInterface(
        currentTemp = state.currentTemp,
        condition = state.condition,
        hourlyTemps = state.hourlyTemps,
    )
}

@Composable
fun WeatherAndWaterInterface(
    currentTemp: Double,
    condition: String,
    hourlyTemps: List<HourBlock>,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                WeatherSummaryCard(currentTemp, condition)
            }

            Box(modifier = Modifier.weight(2f)) {
                GroupedHourlyForecast(hourlyTemps)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun WeatherSummaryCard(temp: Double, condition: String) {
    val colorScheme = MaterialTheme.colorScheme
    val (icon, tint) = when (condition) {
        "clear", "sunny" -> Icons.Default.WbSunny to Color(0xFFFFC107)
        "partly cloudy", "cloudy" -> Icons.Default.Cloud to Color(0xFF90A4AE)
        "fog" -> Icons.Default.VisibilityOff to Color(0xFFB0BEC5)
        "drizzle", "rain", "showers" -> Icons.Default.Umbrella to Color(0xFF2196F3)
        "snow" -> Icons.Default.AcUnit to Color(0xFF80DEEA)
        "thunderstorm" -> Icons.Default.Bolt to Color(0xFFFF5722)
        else -> Icons.Default.Cloud to colorScheme.outline
    }

    val translated = when (condition) {
        "clear", "sunny" -> "Despejado"
        "partly cloudy", "cloudy" -> "Nublado"
        "fog" -> "Niebla"
        "drizzle", "rain", "showers" -> "Lluvia"
        "snow" -> "Nieve"
        "thunderstorm" -> "Tormenta"
        else -> "Condición desconocida"
    }

    val now = Clock.System.now().toEpochMilliseconds()
    val totalMinutes = (now / 1000 / 60) % (24 * 60)
    val hour = (totalMinutes / 60).toInt().toString().padStart(2, '0')
    val minute = (totalMinutes % 60).toInt().toString().padStart(2, '0')
    val formattedTime = "$hour:$minute"

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

@Composable
fun GroupedHourlyForecast(blocks: List<HourBlock>) {
    val grouped = blocks.groupBy { it.startTime.substring(0, 10) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        grouped.forEach { (dateKey, group) ->
            item {
                var expanded by remember { mutableStateOf(false) }

                val avgTemp = group.map { it.temperature }.average().roundTo2Decimals()
                val avgHumidity = group.map { it.humidity }.average().roundToInt()
                val avgPrecip = group.map { it.precipitation }.average().roundTo2Decimals()

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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = formatDate(dateKey),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Promedio: ${avgTemp}°C, $avgHumidity% humedad, ${avgPrecip}mm",
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
                                Spacer(modifier = Modifier.height(8.dp))
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
fun TemperatureBlock(block: HourBlock) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${block.startTime.takeLast(5)} → ${block.endTime.takeLast(5)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Umbrella,
                        contentDescription = "Precipitación",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${block.precipitation.roundTo2Decimals()}mm",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Humedad",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
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
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun Double.roundTo2Decimals(): Double {
    return (this * 100).toInt() / 100.0
}

fun formatDate(isoDate: String): String {
    val (year, month, day) = isoDate.split("-").map { it.toInt() }
    val date = LocalDate(year, month, day)
    val dayName = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "Lunes"
        DayOfWeek.TUESDAY -> "Martes"
        DayOfWeek.WEDNESDAY -> "Miércoles"
        DayOfWeek.THURSDAY -> "Jueves"
        DayOfWeek.FRIDAY -> "Viernes"
        DayOfWeek.SATURDAY -> "Sábado"
        DayOfWeek.SUNDAY -> "Domingo"
        else -> ""
    }
    val monthName = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )[month - 1]
    return "$dayName $day de $monthName"
}
