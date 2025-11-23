package com.dev.jcctech.agroguardian

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.dev.jcctech.agroguardian.data.inyection.LocalDatabase
import com.dev.jcctech.agroguardian.data.inyection.NetworkProvider
import com.dev.jcctech.agroguardian.data.remote.provider.RetrofitProvider
import com.dev.jcctech.agroguardian.data.remote.provider.interceptor.AuthInterceptor
import com.dev.jcctech.agroguardian.data.repository.forum.AndroidForumRepository
import com.dev.jcctech.agroguardian.data.repository.weather.AndroidWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (!granted) {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestLocationPermissions()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Weather Repository
        val weatherDao = LocalDatabase.instance.weatherDao()
        val weatherApiService = RetrofitProvider.weatherApi
        val weatherRepository = AndroidWeatherRepository(weatherApiService, weatherDao)

        // Forum Repository
        val forumDao = LocalDatabase.instance.forumDao()
        val forumApiService = RetrofitProvider.forumApi(AuthInterceptor(NetworkProvider.tokenProvider))
        val forumRepository = AndroidForumRepository(api = forumApiService, dao = forumDao)

        setContent {
            val context = this
            App(
                weatherRepository = weatherRepository,
                forumRepository = forumRepository,
                getCurrentLocation = { getCurrentLocation(context)}
            )
        }

        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)

        for (provider in providers.reversed()) {
            try {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null) {
                    return@withContext location.latitude to location.longitude
                }
            } catch (_: SecurityException) {
                return@withContext null
            }
        }
        null
    }

    private fun requestLocationPermissions() {
        val fine = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}