package com.dev.jcctech.agroguardian.data.remote.provider

import com.dev.jcctech.agroguardian.data.inyection.KotlinxJson
import com.dev.jcctech.agroguardian.data.inyection.NetworkProvider
import com.dev.jcctech.agroguardian.data.remote.provider.interceptor.AuthInterceptor
import com.dev.jcctech.agroguardian.data.remote.provider.token.AndroidTokenProvider
import com.dev.jcctech.agroguardian.data.remote.provider.token.TokenProvider
import com.dev.jcctech.agroguardian.data.remote.service.AuthApiService
import com.dev.jcctech.agroguardian.data.remote.service.ForumApiService
import com.dev.jcctech.agroguardian.data.remote.service.WeatherApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.Interceptor
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.create

object RetrofitProvider {
    const val WEATHER_API_URI = "https://api.open-meteo.com/v1/"
    const val AGRO_GUARDIAN_API_URI = "https://vm.jcctech.dev/"
    private val contentType = "application/json".toMediaType()

    val authApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AGRO_GUARDIAN_API_URI)
            .addConverterFactory(KotlinxJson.asConverterFactory(contentType))
            .build()
            .create(AuthApiService::class.java)
    }

    val weatherApi: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_API_URI)
            .addConverterFactory(KotlinxJson.asConverterFactory(contentType))
            .build()
            .create(WeatherApiService::class.java)
    }

    fun forumApi(authInterceptor: Interceptor): ForumApiService {
        val authClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(AGRO_GUARDIAN_API_URI)
            .client(authClient)
            .addConverterFactory(KotlinxJson.asConverterFactory(contentType))
            .build()
            .create(ForumApiService::class.java)
    }
}