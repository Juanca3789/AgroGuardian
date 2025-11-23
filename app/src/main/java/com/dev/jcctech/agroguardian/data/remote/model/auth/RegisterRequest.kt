package com.dev.jcctech.agroguardian.data.remote.model.auth

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)