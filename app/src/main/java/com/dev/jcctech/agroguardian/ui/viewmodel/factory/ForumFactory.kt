package com.dev.jcctech.agroguardian.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.dev.jcctech.agroguardian.data.repository.forum.ForumRepository
import com.dev.jcctech.agroguardian.ui.viewmodel.ForumViewModel
import com.dev.jcctech.agroguardian.ui.viewmodel.QuestionDetailViewModel

class ForumFactory(
    private val repository: ForumRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()

        return when {
            modelClass.isAssignableFrom(ForumViewModel::class.java) -> {
                ForumViewModel(repository) as T
            }
            modelClass.isAssignableFrom(QuestionDetailViewModel::class.java) -> {
                QuestionDetailViewModel(repository, savedStateHandle) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}