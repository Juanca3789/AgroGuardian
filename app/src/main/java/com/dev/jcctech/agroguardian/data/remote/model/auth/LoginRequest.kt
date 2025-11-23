package com.dev.jcctech.agroguardian.data.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val userName: String,
    val password: String
)