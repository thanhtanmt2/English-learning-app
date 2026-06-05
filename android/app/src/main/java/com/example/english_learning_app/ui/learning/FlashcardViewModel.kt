package com.example.english_learning_app.ui.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlashcardUiState(
    val isLoading: Boolean = false,
    val wordSets: List<WordSet> = emptyList(),
    val wordSet: WordSet? = null,
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val errorMessage: String? = null
)

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: VocabularyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    fun load() {
        if (_uiState.value.isLoading || _uiState.value.wordSets.isNotEmpty()) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val wordSets = repository.loadWordSets()
                _uiState.update { it.copy(isLoading = false, wordSets = wordSets) }
            } catch (ex: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = ex.message) }
            }
        }
    }

    fun selectWordSet(wordSet: WordSet) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val words = repository.loadWords(wordSetId = wordSet.id.toString())
                _uiState.update { it.copy(isLoading = false, wordSet = wordSet, words = words, currentIndex = 0) }
            } catch (ex: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = ex.message) }
            }
        }
    }

    fun selectWordSetById(wordSetId: String) {
        val set = _uiState.value.wordSets.firstOrNull { it.id.toString() == wordSetId } ?: return
        selectWordSet(set)
    }

    fun clearSelection() {
        _uiState.update { it.copy(wordSet = null, words = emptyList(), currentIndex = 0) }
    }

    fun nextWord() {
        val state = _uiState.value
        if (state.words.isEmpty()) return
        _uiState.update { it.copy(currentIndex = (state.currentIndex + 1) % state.words.size) }
    }

    fun previousWord() {
        val state = _uiState.value
        if (state.words.isEmpty()) return
        val prev = if (state.currentIndex - 1 < 0) state.words.size - 1 else state.currentIndex - 1
        _uiState.update { it.copy(currentIndex = prev) }
    }

    fun submitReview(quality: Int) {
        val state = _uiState.value
        val word = state.words.getOrNull(state.currentIndex) ?: return
        viewModelScope.launch {
            try {
                repository.submitReview(word.id, quality)
                // Optimistic update: đánh dấu từ đã học ngay nếu quality >= 3
                if (quality >= 3) {
                    val updatedWords = state.words.toMutableList().also { list ->
                        val idx = list.indexOfFirst { it.id == word.id }
                        if (idx >= 0) list[idx] = list[idx].copy(intervalDays = 2)
                    }
                    _uiState.update { it.copy(words = updatedWords) }
                }
            } catch (ex: Exception) {
                android.util.Log.e("FlashcardViewModel", "submitReview error: ${ex.message}")
            }
        }
    }
}
