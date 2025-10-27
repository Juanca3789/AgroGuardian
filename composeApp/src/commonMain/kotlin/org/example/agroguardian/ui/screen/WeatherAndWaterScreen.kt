package org.example.agroguardian.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.agroguardian.data.inyection.AppProviders
import org.example.agroguardian.ui.viewModel.HourBlock
import org.example.agroguardian.ui.viewModel.WeatherAndWaterViewModel

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
        hourlyTemps = state.hourlyTemps
    )
}

@Composable
fun WeatherAndWaterInterface(
    currentTemp: Double,
    condition: String,
    hourlyTemps: List<HourBlock>
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                WeatherIcon(condition)
                Text(
                    text = "${currentTemp}°C",
                    fontSize = 32.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {
                items(hourlyTemps) { block ->
                    TemperatureBlock(block)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun WeatherIcon(condition: String) {
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

    Icon(
        imageVector = icon,
        contentDescription = condition,
        tint = tint,
        modifier = Modifier.size(120.dp)
    )
}

@Composable
fun TemperatureBlock(block: HourBlock) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("${block.startTime} - ${block.endTime}", fontSize = 16.sp)
                Text("Humedad: ${block.humidity}%", fontSize = 14.sp)
                Text("Precipitación: ${block.precipitation}mm", fontSize = 14.sp)
            }
            Text("${block.temperature}°C", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
