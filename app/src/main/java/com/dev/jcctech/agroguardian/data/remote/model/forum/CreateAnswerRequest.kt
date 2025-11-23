package com.dev.jcctech.agroguardian.data.remote.model.forum

import kotlinx.serialization.Serializable

@Serializable
data class CreateAnswerRequest(
    val content: String
)