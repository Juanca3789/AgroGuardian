package com.dev.jcctech.agroguardian.data.repository.forum

import com.dev.jcctech.agroguardian.data.db.entity.AnswerEntity
import com.dev.jcctech.agroguardian.data.db.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

interface ForumRepository {

    fun getAllQuestions(): Flow<List<QuestionEntity>>

    fun getAnswersForQuestion(questionId: String): Flow<List<AnswerEntity>>

    suspend fun createQuestion(title: String, content: String, author: String)

    suspend fun createAnswer(questionId: String, content: String, author: String)

    suspend fun syncForum()

}