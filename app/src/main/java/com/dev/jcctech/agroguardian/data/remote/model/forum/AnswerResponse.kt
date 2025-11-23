package com.dev.jcctech.agroguardian.data.remote.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnswerResponse(
    val id: Long,
    @SerialName("question_id") val questionId: Long,
    @SerialName("user_id") val userId: Long,
    val author: String,
    val content: String,
    @SerialName("created_at") val createdAt: Long
)
