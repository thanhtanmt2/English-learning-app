package com.example.english_learning_app.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.WordPayload
import com.example.english_learning_app.data.remote.RetrofitProvider
import com.example.english_learning_app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditWordViewModel : ViewModel() {
    private val repository = VocabularyRepository(RetrofitProvider.apiService)

    private val _uiState = MutableStateFlow(AddEditWordUiState())
    val uiState: StateFlow<AddEditWordUiState> = _uiState.asStateFlow()

    fun load(wordSetId: String, wordId: String?) {
        if (_uiState.value.wordSetId == wordSetId && _uiState.value.wordId == wordId) {
            return
        }
        _uiState.value = AddEditWordUiState(wordSetId = wordSetId, wordId = wordId)
        if (wordId.isNullOrBlank()) {
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val word = repository.loadWord(wordId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    word = word.word,
                    meaning = word.meaning,
                    example = word.example ?: ""
                )
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = ex.message)
            }
        }
    }

    fun updateWord(value: String) {
        _uiState.value = _uiState.value.copy(word = value)
    }

    fun updateMeaning(value: String) {
        _uiState.value = _uiState.value.copy(meaning = value)
    }

    fun updateExample(value: String) {
        _uiState.value = _uiState.value.copy(example = value)
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.wordSetId.isBlank()) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            try {
                val user = repository.loadUser()
                val payload = WordPayload(
                    wordsetId = state.wordSetId,
                    userId = user.id,
                    word = state.word.trim(),
                    meaning = state.meaning.trim(),
                    example = state.example.trim().ifBlank { null }
                )
                if (state.wordId.isNullOrBlank()) {
                    repository.createWord(payload)
                } else {
                    repository.updateWord(state.wordId, payload)
                }
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = ex.message)
            }
        }
    }
}

data class AddEditWordUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val wordSetId: String = "",
    val wordId: String? = null,
    val word: String = "",
    val meaning: String = "",
    val example: String = "",
    val errorMessage: String? = null
)
