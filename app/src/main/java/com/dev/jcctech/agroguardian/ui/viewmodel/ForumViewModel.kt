package com.dev.jcctech.agroguardian.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.jcctech.agroguardian.data.db.entity.QuestionEntity
import com.dev.jcctech.agroguardian.data.repository.forum.ForumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForumUiState(
    val questions: List<QuestionEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null
)

class ForumViewModel(private val forumRepository: ForumRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ForumUiState())
    val uiState: StateFlow<ForumUiState> = _uiState.asStateFlow()

    init {
        // Suscribe to local data changes
        forumRepository.getAllQuestions()
            .onEach { questions ->
                _uiState.update { it.copy(questions = questions, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar las preguntas desde la base de datos.") }
            }
            .launchIn(viewModelScope)

        // Initial sync
        syncForum()
    }

    fun createQuestion(title: String, content: String) {
        viewModelScope.launch {
            // The 'author' parameter was removed from the repository method
            forumRepository.createQuestion(title, content, author = "Anonimo")
        }
    }

    fun syncForum() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            try {
                forumRepository.syncForum()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error de conexión. No se pudo sincronizar.") }
            }
            finally {
                _uiState.update { it.copy(isSyncing = false) }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}