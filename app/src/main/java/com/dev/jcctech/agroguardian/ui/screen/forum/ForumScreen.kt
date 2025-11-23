package com.dev.jcctech.agroguardian.ui.screen.forum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev.jcctech.agroguardian.data.db.entity.QuestionEntity
import com.dev.jcctech.agroguardian.data.inyection.AppProviders
import com.dev.jcctech.agroguardian.ui.viewmodel.factory.ForumFactory
import com.dev.jcctech.agroguardian.ui.viewmodel.ForumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    onAskQuestion: () -> Unit,
    onQuestionClick: (String) -> Unit,
) {
    val repository = AppProviders.LocalForumRepository.current
    val factory = remember(repository) {
        ForumFactory(repository)
    }
    val viewModel: ForumViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Foro") },
                actions = {
                    IconButton(onClick = { viewModel.syncForum() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sincronizar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAskQuestion) {
                Icon(Icons.Default.Add, contentDescription = "Hacer una pregunta")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.questions.isEmpty()) {
                Text("No hay preguntas todavía. ¡Sé el primero!", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.questions, key = { it.id }) {
                        QuestionItem(question = it, onClick = { onQuestionClick(it.id) })
                    }
                }
            }

            if (uiState.isSyncing && !uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun QuestionItem(question: QuestionEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = question.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "por ${question.author}", style = MaterialTheme.typography.bodySmall)
        }
    }
}