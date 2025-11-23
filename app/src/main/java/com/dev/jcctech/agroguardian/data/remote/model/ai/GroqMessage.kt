package com.dev.jcctech.agroguardian.data.remote.model.ai

import kotlinx.serialization.Serializable

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)
