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
        if (_uiState.value.isLoading || _uiState.value.wordSets.isNotEmpty()) {
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val user = repository.loadUser()
                val wordSets = repository.loadWordSets(user.id)
                _uiState.value = LearningUiState(
                    isLoading = false,
                    userId = user.id,
                    wordSets = wordSets
                )
            } catch (ex: Exception) {
                _uiState.value = LearningUiState(isLoading = false, errorMessage = ex.message)
            }
        }
    }

    fun selectWordSet(wordSet: WordSet) {
        val userId = _uiState.value.userId ?: return
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val words = repository.loadWords(userId = userId, wordSetId = wordSet.id)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    wordSet = wordSet,
                    words = words,
                    currentIndex = 0
                )
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = ex.message)
            }
        }
    }

    fun selectWordSetById(wordSetId: String) {
        val set = _uiState.value.wordSets.firstOrNull { it.id == wordSetId } ?: return
        selectWordSet(set)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(wordSet = null, words = emptyList(), currentIndex = 0)
    }

    fun nextWord() {
        val state = _uiState.value
        if (state.words.isEmpty()) return
        val nextIndex = (state.currentIndex + 1) % state.words.size
        _uiState.value = state.copy(currentIndex = nextIndex)
    }

    fun previousWord() {
        val state = _uiState.value
        if (state.words.isEmpty()) return
        val prevIndex = if (state.currentIndex - 1 < 0) state.words.size - 1 else state.currentIndex - 1
        _uiState.value = state.copy(currentIndex = prevIndex)
    }
}

data class LearningUiState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val wordSets: List<WordSet> = emptyList(),
    val wordSet: WordSet? = null,
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val errorMessage: String? = null
)
