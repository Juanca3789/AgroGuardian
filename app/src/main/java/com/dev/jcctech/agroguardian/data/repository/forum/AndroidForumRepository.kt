package com.dev.jcctech.agroguardian.data.repository.forum

import android.util.Log
import com.dev.jcctech.agroguardian.data.db.dao.ForumDao
import com.dev.jcctech.agroguardian.data.db.entity.AnswerEntity
import com.dev.jcctech.agroguardian.data.db.entity.QuestionEntity
import com.dev.jcctech.agroguardian.data.remote.model.forum.CreateAnswerRequest
import com.dev.jcctech.agroguardian.data.remote.model.forum.CreateQuestionRequest
import com.dev.jcctech.agroguardian.data.remote.model.forum.toEntity
import com.dev.jcctech.agroguardian.data.remote.service.ForumApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class AndroidForumRepository(
    private val dao: ForumDao,
    private val api: ForumApiService
) : ForumRepository {

    override fun getAllQuestions(): Flow<List<QuestionEntity>> {
        return dao.getAllQuestions()
    }

    override fun getAnswersForQuestion(questionId: String): Flow<List<AnswerEntity>> {
        return dao.getAnswersForQuestion(questionId)
    }

    override suspend fun createQuestion(title: String, content: String, author: String) {
        val tempId = UUID.randomUUID().toString()
        val newQuestion = QuestionEntity(
            id = tempId,
            title = title,
            content = content,
            author = author,
            createdAt = System.currentTimeMillis(),
            syncStatus = "PENDING"
        )
        dao.insertQuestion(newQuestion)
        syncForum()
    }

    override suspend fun createAnswer(questionId: String, content: String, author: String) {
        val tempId = UUID.randomUUID().toString()
        val newAnswer = AnswerEntity(
            id = tempId,
            questionId = questionId,
            content = content,
            author = author,
            createdAt = System.currentTimeMillis(),
            syncStatus = "PENDING"
        )
        dao.insertAnswer(newAnswer)
        // La sincronización se inicia inmediatamente después de crear la respuesta
        syncForum()
    }

    override suspend fun syncForum() {
        try {
            uploadPendingQuestions()
            uploadPendingAnswers()
            fetchAndSaveRemoteQuestions()
            // Podríamos optimizar esto para bajar solo respuestas de preguntas visibles
            dao.getAllQuestions().first().forEach { fetchAndSaveRemoteAnswers(it.id) }
        } catch (e: Exception) {
            Log.e("ForumRepository", "Error al sincronizar el foro", e)
        }
    }

    private suspend fun uploadPendingQuestions() {
        val pendingQuestions = dao.getQuestionsByStatus("PENDING")
        for (question in pendingQuestions) {
            val request = CreateQuestionRequest(question.title, question.content)
            val syncedQuestion = api.createQuestion(request)
            val newEntity = syncedQuestion.toEntity()

            dao.deleteQuestion(question)
            dao.insertQuestion(newEntity)
            dao.updateAnswersQuestionId(oldQuestionId = question.id, newQuestionId = newEntity.id)
        }
    }

    private suspend fun uploadPendingAnswers() {
        val pendingAnswers = dao.getAnswersByStatus("PENDING")
        for (answer in pendingAnswers) {
            val questionIdAsLong = answer.questionId.toLongOrNull()
            if (questionIdAsLong != null) {
                val request = CreateAnswerRequest(answer.content)
                val syncedAnswer = api.createAnswer(questionIdAsLong, request)
                dao.deleteAnswer(answer)
                dao.insertAnswer(syncedAnswer.toEntity())
            }
        }
    }

    private suspend fun fetchAndSaveRemoteQuestions() {
        val remoteQuestions = api.getQuestions()
        val entities = remoteQuestions.map { it.toEntity() }
        dao.insertQuestions(entities)
    }

    private suspend fun fetchAndSaveRemoteAnswers(questionId: String) {
        val questionIdAsLong = questionId.toLongOrNull() ?: return
        val remoteAnswers = api.getAnswers(questionIdAsLong)
        val entities = remoteAnswers.map { it.toEntity() }
        dao.insertAnswers(entities)
    }
}
