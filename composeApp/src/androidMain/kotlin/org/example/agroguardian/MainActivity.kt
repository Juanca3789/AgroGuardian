package org.example.agroguardian

import android.Manifest
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.agroguardian.data.db.AppDatabase
import org.example.agroguardian.data.repository.AndroidWeatherRepository
import kotlin.coroutines.resume

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val driver = AndroidSqliteDriver(AppDatabase.Schema, context = this, name = null)
        val database = AppDatabase(driver)
        val weatherRepository = AndroidWeatherRepository()

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        val getCurrentLocation: suspend () -> Pair<Double, Double>? = {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            suspendCancellableCoroutine { cont ->
                val providers = listOf(
                    LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER
                )
                for (provider in providers) {
                    try {
                        val location: Location? = locationManager.getLastKnownLocation(provider)
                        if (location != null) {
                            cont.resume(Pair(location.latitude, location.longitude))
                            return@suspendCancellableCoroutine
                        }
                    } catch (_: SecurityException) {
                        cont.resume(null)
                        return@suspendCancellableCoroutine
                    }
                }
                cont.resume(null)
            }
        }

        setContent {
            App(
                appDatabase = database,
                weatherRepository = weatherRepository,
                getCurrentLocation = getCurrentLocation
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(
        appDatabase = null,
        weatherRepository = null,
        getCurrentLocation = { null }
    )
}