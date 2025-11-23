package com.dev.jcctech.agroguardian.data.remote.model.ai

import kotlinx.serialization.Serializable

@Serializable
data class GroqResponse(
    val id: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<GroqChoice>
)
