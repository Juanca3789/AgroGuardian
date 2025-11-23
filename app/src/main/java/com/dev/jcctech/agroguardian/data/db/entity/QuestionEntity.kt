package com.dev.jcctech.agroguardian.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.ExperimentalTime

@Entity(tableName = "questions")
data class QuestionEntity @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val createdAt: Long,
    val syncStatus: String
)