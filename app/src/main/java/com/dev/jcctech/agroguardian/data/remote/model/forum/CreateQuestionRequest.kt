package com.dev.jcctech.agroguardian.data.remote.model.forum

import kotlinx.serialization.Serializable

@Serializable
data class CreateQuestionRequest(
    val title: String,
    val content: String
)