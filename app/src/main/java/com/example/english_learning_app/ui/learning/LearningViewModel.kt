package com.example.english_learning_app.ui.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.remote.RetrofitProvider
import com.example.english_learning_app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LearningViewModel : ViewModel() {
    private val repository = VocabularyRepository(RetrofitProvider.apiService)

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

    fun load() {
        if (_uiState.value.isLoading || _uiState.value.wordSet != null) {
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val user = repository.loadUser()
                val wordSets = repository.loadWordSets(user.id)
                val firstSet = wordSets.firstOrNull()
                if (firstSet == null) {
                    _uiState.value = LearningUiState(isLoading = false, errorMessage = "No word sets")
                    return@launch
                }
                val words = repository.loadWords(userId = user.id, wordSetId = firstSet.id)
                _uiState.value = LearningUiState(
                    isLoading = false,
                    wordSet = firstSet,
                    words = words
                )
            } catch (ex: Exception) {
                _uiState.value = LearningUiState(isLoading = false, errorMessage = ex.message)
            }
        }
    }

    fun nextWord() {
        val state = _uiState.value
        if (state.words.isEmpty()) return
        val nextIndex = (state.currentIndex + 1) % state.words.size
        _uiState.value = state.copy(currentIndex = nextIndex)
    }
}

data class LearningUiState(
    val isLoading: Boolean = false,
    val wordSet: WordSet? = null,
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val errorMessage: String? = null
)
