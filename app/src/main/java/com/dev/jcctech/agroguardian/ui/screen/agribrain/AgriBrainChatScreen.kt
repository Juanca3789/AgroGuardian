package com.dev.jcctech.agroguardian.ui.screen.agribrain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev.jcctech.agroguardian.data.remote.model.ai.GroqMessage
import com.dev.jcctech.agroguardian.ui.viewmodel.ChatViewModel

@Composable
fun AgriBrainChatScreen(
    chatViewModel: ChatViewModel = viewModel()
) {
    val messages by chatViewModel.messages.collectAsState()
    var userInput by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .padding(12.dp)
    ) {

        // LISTA DE MENSAJES
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                MessageBubble(message = msg)
            }
        }

        // BARRA DE ESCRITURA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe tu mensaje...") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (userInput.text.isNotBlank()) {
                        chatViewModel.sendMessage(userInput.text)
                        userInput = TextFieldValue("")
                    }
                }
            ) {
                Text("Enviar")
            }
        }
    }
}

@Composable
fun MessageBubble(message: GroqMessage) {

    val isUser = message.role == "user"

    val bubbleColor = if (isUser) Color(0xFFDCF8C6) else Color.White
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .padding(6.dp)
                .background(bubbleColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(message.content)
        }
    }
}
