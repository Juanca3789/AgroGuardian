package com.dev.jcctech.agroguardian.data.remote.service

import com.dev.jcctech.agroguardian.data.remote.model.forum.AnswerResponse
import com.dev.jcctech.agroguardian.data.remote.model.forum.CreateAnswerRequest
import com.dev.jcctech.agroguardian.data.remote.model.forum.CreateQuestionRequest
import com.dev.jcctech.agroguardian.data.remote.model.forum.QuestionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ForumApiService {

    @GET("forum/questions")
    suspend fun getQuestions(): List<QuestionResponse>

    @POST("forum/questions")
    suspend fun createQuestion(@Body request: CreateQuestionRequest): QuestionResponse

    @GET("forum/questions/{id}/answers")
    suspend fun getAnswers(@Path("id") questionId: Long): List<AnswerResponse>

    @POST("forum/questions/{id}/answers")
    suspend fun createAnswer(
        @Path("id") questionId: Long,
        @Body request: CreateAnswerRequest
    ): AnswerResponse

}
