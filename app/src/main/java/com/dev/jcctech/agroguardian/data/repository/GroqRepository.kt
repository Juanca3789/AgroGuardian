package com.dev.jcctech.agroguardian.data.repository

import com.dev.jcctech.agroguardian.data.remote.model.ai.GroqRequest
import com.dev.jcctech.agroguardian.data.remote.model.ai.GroqResponse
import com.dev.jcctech.agroguardian.data.remote.provider.GroqRetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroqRepository {

    private val service = GroqRetrofitProvider.groqService

    suspend fun sendMessage(request: GroqRequest): GroqResponse? {
        return withContext(Dispatchers.IO) {
            try {
                service.getChatCompletion(request)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

