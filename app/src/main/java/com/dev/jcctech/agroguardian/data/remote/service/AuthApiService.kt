package com.dev.jcctech.agroguardian.data.remote.service

import com.dev.jcctech.agroguardian.data.remote.model.auth.LoginRequest
import com.dev.jcctech.agroguardian.data.remote.model.auth.RefreshRequest
import com.dev.jcctech.agroguardian.data.remote.model.auth.RegisterRequest
import com.dev.jcctech.agroguardian.data.remote.model.auth.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): TokenResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): TokenResponse
}