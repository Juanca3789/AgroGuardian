package com.dev.jcctech.agroguardian.data.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)