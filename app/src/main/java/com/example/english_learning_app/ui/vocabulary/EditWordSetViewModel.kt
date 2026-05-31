package com.example.english_learning_app.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.WordSetPayload
import com.example.english_learning_app.data.remote.RetrofitProvider
import com.example.english_learning_app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditWordSetViewModel : ViewModel() {
    private val repository = VocabularyRepository(RetrofitProvider.apiService)

    private val _uiState = MutableStateFlow(EditWordSetUiState())
    val uiState: StateFlow<EditWordSetUiState> = _uiState.asStateFlow()

    fun load(wordSetId: String) {
        if (_uiState.value.wordSetId == wordSetId && _uiState.value.name.isNotBlank()) {
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val wordSet = repository.loadWordSet(wordSetId)
                _uiState.value = EditWordSetUiState(
                    isLoading = false,
                    wordSetId = wordSet.id,
                    userId = wordSet.userId,
                    name = wordSet.name,
                    description = wordSet.description,
                    tags = wordSet.tags.joinToString(", ")
                )
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = ex.message
                )
            }
        }
    }

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
        if (state.wordSetId.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Khong tim thay word set")
            return
        }
        if (state.name.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Name is required")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            try {
                val payload = WordSetPayload(
                    userId = state.userId,
                    name = state.name.trim(),
                    description = state.description.trim(),
                    tags = state.tags.split(',').map { it.trim() }.filter { it.isNotBlank() }
                )
                repository.updateWordSet(state.wordSetId, payload)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = ex.message)
            }
        }
    }
}

data class EditWordSetUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val wordSetId: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val tags: String = "",
    val errorMessage: String? = null
)
