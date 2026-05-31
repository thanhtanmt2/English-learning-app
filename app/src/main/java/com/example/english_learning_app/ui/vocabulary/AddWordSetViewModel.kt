package com.example.english_learning_app.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.WordSetPayload
import com.example.english_learning_app.data.remote.RetrofitClient
import com.example.english_learning_app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddWordSetViewModel : ViewModel() {
    private val repository = VocabularyRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(AddWordSetUiState())
    val uiState: StateFlow<AddWordSetUiState> = _uiState.asStateFlow()

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun updateTags(value: String) {
        _uiState.value = _uiState.value.copy(tags = value)
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Name is required")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            try {
                val user = repository.loadUser()
                val payload = WordSetPayload(
                    userId = user.id,
                    name = state.name.trim(),
                    description = state.description.trim(),
                    tags = state.tags.split(',').map { it.trim() }.filter { it.isNotBlank() }
                )
                repository.createWordSet(payload)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = ex.message)
            }
        }
    }
}

data class AddWordSetUiState(
    val isSaving: Boolean = false,
    val name: String = "",
    val description: String = "",
    val tags: String = "",
    val errorMessage: String? = null
)
