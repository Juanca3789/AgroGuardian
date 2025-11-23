package com.dev.jcctech.agroguardian.data.remote.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val id: Long,
    @SerialName("user_id") val userId: Long,
    val author: String,
    val title: String,
    val content: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("answer_count") val answerCount: Int
)