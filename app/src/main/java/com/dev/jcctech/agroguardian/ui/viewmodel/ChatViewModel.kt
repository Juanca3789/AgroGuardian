package com.dev.jcctech.agroguardian.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.jcctech.agroguardian.data.remote.model.ai.GroqMessage
import com.dev.jcctech.agroguardian.data.remote.model.ai.GroqRequest
import com.dev.jcctech.agroguardian.data.repository.GroqRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = GroqRepository()

    private val _messages = MutableStateFlow<List<GroqMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    // PROMPT para que SOLO responda sobre agricultura
    private val systemPrompt = GroqMessage(
        role = "system",
        content = """
            Eres un asistente experto solo en AGRICULTURA.
            
            - Solo puedes responder preguntas relacionadas con cultivos, fertilizantes, plagas, riegos, tierra, semillas, clima agrícola y producción agrícola.
            - Si el usuario pide recetas, matemáticas, historias, chistes, programación u otros temas NO agrícolas, responde: 
              "Lo siento, solo puedo responder preguntas relacionadas con agricultura."
        """.trimIndent()
    )

    fun sendMessage(text: String) {
        viewModelScope.launch {
            // Agregar mensaje del usuario
            val userMessage = GroqMessage("user", text)
            val currentMessages = _messages.value.toMutableList()
            currentMessages.add(userMessage)

            // Crear request con historial + prompt del sistema
            val request = GroqRequest(
                model = "grok-4-latest",
                messages = listOf(systemPrompt) + currentMessages
            )

            // Llamada al repositorio
            val response = repository.sendMessage(request)

            val reply = response?.choices?.firstOrNull()?.message

            if (reply != null) {
                currentMessages.add(reply)
            }

            _messages.value = currentMessages
        }
    }
}
