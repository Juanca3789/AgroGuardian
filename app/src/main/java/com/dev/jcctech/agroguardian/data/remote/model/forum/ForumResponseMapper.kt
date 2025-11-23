package com.dev.jcctech.agroguardian.data.remote.model.forum

import com.dev.jcctech.agroguardian.data.db.entity.AnswerEntity
import com.dev.jcctech.agroguardian.data.db.entity.QuestionEntity
import com.dev.jcctech.agroguardian.data.db.entity.SyncStatus

fun QuestionResponse.toEntity(): QuestionEntity {
    return QuestionEntity(
        id = this.id.toString(),
        title = this.title,
        content = this.content,
        author = this.author,
        createdAt = this.createdAt,
        syncStatus = SyncStatus.SYNCED.name
    )
}

fun AnswerResponse.toEntity(): AnswerEntity {
    return AnswerEntity(
        id = this.id.toString(),
        questionId = this.questionId.toString(),
        content = this.content,
        author = this.author,
        createdAt = this.createdAt,
        syncStatus = SyncStatus.SYNCED.name
    )
}
