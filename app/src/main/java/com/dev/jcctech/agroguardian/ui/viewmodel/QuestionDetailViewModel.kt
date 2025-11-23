package com.dev.jcctech.agroguardian.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.jcctech.agroguardian.data.db.entity.AnswerEntity
import com.dev.jcctech.agroguardian.data.db.entity.QuestionEntity
import com.dev.jcctech.agroguardian.data.repository.forum.ForumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuestionDetailUiState(
    val question: QuestionEntity? = null,
    val answers: List<AnswerEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null
)

class QuestionDetailViewModel(
    private val forumRepository: ForumRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val questionId: String = savedStateHandle.get<String>("questionId")!!

    private val _uiState = MutableStateFlow(QuestionDetailUiState())
    val uiState: StateFlow<QuestionDetailUiState> = _uiState.asStateFlow()

    init {
        val questionFlow = forumRepository.getAllQuestions().map { questions ->
            questions.firstOrNull { it.id == questionId }
        }
        val answersFlow = forumRepository.getAnswersForQuestion(questionId)

        combine(questionFlow, answersFlow) { question, answers ->
            QuestionDetailUiState(question = question, answers = answers, isLoading = false)
        }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar el detalle.") }
            }
            .onEach { state -> _uiState.value = state }
            .launchIn(viewModelScope)

        syncQuestionDetails()
    }

    fun postAnswer(content: String) {
        viewModelScope.launch {
            forumRepository.createAnswer(
                questionId, content,
                author = "Anonimo"
            )
        }
    }

    fun syncQuestionDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            try {
                forumRepository.syncForum() // A full sync also updates details
            } catch (e: Exception) {
                // Specific error handling can be improved here
                _uiState.update { it.copy(errorMessage = "Error de conexión.") }
            } finally {
                _uiState.update { it.copy(isSyncing = false) }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
