package com.dev.jcctech.agroguardian.data.remote.model.ai

import kotlinx.serialization.Serializable

@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.3
)
