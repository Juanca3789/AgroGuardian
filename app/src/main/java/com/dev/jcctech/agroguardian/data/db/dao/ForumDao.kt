package com.dev.jcctech.agroguardian.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev.jcctech.agroguardian.data.db.entity.AnswerEntity
import com.dev.jcctech.agroguardian.data.db.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForumDao {

    // --- Question Queries ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Query("SELECT * FROM questions ORDER BY createdAt DESC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE syncStatus = :status")
    suspend fun getQuestionsByStatus(status: String): List<QuestionEntity>

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)

    // --- Answer Queries ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AnswerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: AnswerEntity)

    @Query("SELECT * FROM answers WHERE questionId = :questionId ORDER BY createdAt ASC")
    fun getAnswersForQuestion(questionId: String): Flow<List<AnswerEntity>>

    @Query("SELECT * FROM answers WHERE syncStatus = :status")
    suspend fun getAnswersByStatus(status: String): List<AnswerEntity>

    @Delete
    suspend fun deleteAnswer(answer: AnswerEntity)

    // --- Sync Logic Specific Queries ---

    /**
     * Finds all answers pointing to a temporary question ID and updates them
     * to point to the new, permanent ID received from the server.
     * This is crucial to avoid orphan answers after a sync.
     */
    @Query("UPDATE answers SET questionId = :newQuestionId WHERE questionId = :oldQuestionId")
    suspend fun updateAnswersQuestionId(oldQuestionId: String, newQuestionId: String)
}
