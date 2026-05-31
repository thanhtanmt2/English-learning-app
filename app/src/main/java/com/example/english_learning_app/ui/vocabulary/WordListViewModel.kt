package com.example.english_learning_app.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.remote.RetrofitClient
import com.example.english_learning_app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WordListViewModel : ViewModel() {
    private val repository = VocabularyRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(WordListUiState())
    val uiState: StateFlow<WordListUiState> = _uiState.asStateFlow()

    fun load(wordSetId: String, force: Boolean = false) {
        if (!force && (_uiState.value.isLoading || _uiState.value.wordSetId == wordSetId)) {
            return
        }
        _uiState.value = WordListUiState(isLoading = true, wordSetId = wordSetId)
        viewModelScope.launch {
            try {
                val user = repository.loadUser()
                val words = repository.loadWords(userId = user.id, wordSetId = wordSetId)
                _uiState.value = WordListUiState(isLoading = false, wordSetId = wordSetId, words = words)
            } catch (ex: Exception) {
                _uiState.value = WordListUiState(
                    isLoading = false,
                    wordSetId = wordSetId,
                    errorMessage = ex.message
                )
            }
        }
    }

    fun deleteWord(wordId: String) {
        val wordSetId = _uiState.value.wordSetId ?: return
        viewModelScope.launch {
            try {
                repository.deleteWord(wordId)
                load(wordSetId, force = true)
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = ex.message)
            }
        }
    }
}

data class WordListUiState(
    val isLoading: Boolean = false,
    val wordSetId: String? = null,
    val words: List<Word> = emptyList(),
    val errorMessage: String? = null
)
