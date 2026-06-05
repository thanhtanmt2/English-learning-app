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

enum class DictationFeedback { CORRECT, WRONG }

data class DictationUiState(
    val isLoading: Boolean = false,
    val wordSets: List<WordSet> = emptyList(),
    val wordSet: WordSet? = null,
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val errorMessage: String? = null,
    // state riêng của Dictation
    val answer: String = "",
    val feedback: DictationFeedback? = null,
    val sessionScore: Int = 0,
    val sessionTotal: Int = 0
)

@HiltViewModel
class DictationViewModel @Inject constructor(
    private val repository: VocabularyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DictationUiState())
    val uiState: StateFlow<DictationUiState> = _uiState.asStateFlow()

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
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        wordSet = wordSet,
                        words = words,
                        currentIndex = 0,
                        answer = "",
                        feedback = null,
                        sessionScore = 0,
                        sessionTotal = 0
                    )
                }
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
        _uiState.update {
            it.copy(
                wordSet = null, words = emptyList(), currentIndex = 0,
                answer = "", feedback = null, sessionScore = 0, sessionTotal = 0
            )
        }
    }

    fun nextWord() {
        val state = _uiState.value
        if (state.words.isEmpty()) return
        _uiState.update {
            it.copy(
                currentIndex = (state.currentIndex + 1) % state.words.size,
                answer = "",
                feedback = null
            )
        }
    }

    fun previousWord() {
        val state = _uiState.value
        if (state.words.isEmpty()) return
        val prev = if (state.currentIndex - 1 < 0) state.words.size - 1 else state.currentIndex - 1
        _uiState.update { it.copy(currentIndex = prev, answer = "", feedback = null) }
    }

    fun updateAnswer(text: String) {
        _uiState.update { it.copy(answer = text, feedback = null) }
    }

    fun checkAnswer() {
        val state = _uiState.value
        val word = state.words.getOrNull(state.currentIndex) ?: return
        if (state.answer.isBlank()) return

        val isCorrect = state.answer.trim().lowercase() == word.word.trim().lowercase()
        val quality = if (isCorrect) 5 else 0

        viewModelScope.launch {
            try {
                repository.submitReview(word.id, quality)
                // Optimistic update: đánh dấu đã học nếu trả lời đúng
                if (isCorrect) {
                    val updatedWords = state.words.toMutableList().also { list ->
                        val idx = list.indexOfFirst { it.id == word.id }
                        if (idx >= 0) list[idx] = list[idx].copy(intervalDays = 2)
                    }
                    _uiState.update { it.copy(words = updatedWords) }
                }
            } catch (ex: Exception) {
                android.util.Log.e("DictationViewModel", "submitReview error: ${ex.message}")
            }
        }

        _uiState.update {
            it.copy(
                feedback = if (isCorrect) DictationFeedback.CORRECT else DictationFeedback.WRONG,
                sessionScore = if (isCorrect) it.sessionScore + 1 else it.sessionScore,
                sessionTotal = it.sessionTotal + 1
            )
        }
    }
}
