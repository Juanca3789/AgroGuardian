package com.dev.jcctech.agroguardian.data.remote.provider

import com.dev.jcctech.agroguardian.data.remote.service.GroqService
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GroqAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer appi")
            .addHeader("Content-Type", "application/json")
            .build()

        return chain.proceed(request)
    }
}

object GroqRetrofitProvider {

    private val client = OkHttpClient.Builder()
        .addInterceptor(GroqAuthInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.x.ai/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val groqService: GroqService = retrofit.create(GroqService::class.java)
}
