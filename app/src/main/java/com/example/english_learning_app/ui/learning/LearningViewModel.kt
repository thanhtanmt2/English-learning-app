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
    private val repository = VocabularyRepository(com.example.english_learning_app.data.remote.RetrofitClient.apiService)

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
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userId = user.id,
                    wordSets = wordSets
                )
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = ex.message)
            }
        }
    }

    fun selectWordSet(wordSet: WordSet) {
        val userId = _uiState.value.userId ?: return
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val words = repository.loadWords(userId = userId, wordSetId = wordSet.id.toString())
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
        val set = _uiState.value.wordSets.firstOrNull { it.id.toString() == wordSetId } ?: return
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

    fun submitReview(quality: Int) {
        val word = _uiState.value.words.getOrNull(_uiState.value.currentIndex) ?: return
        viewModelScope.launch {
            try {
                repository.submitReview(word.id, quality)
            } catch (ex: Exception) {
                // Ta không chặn UI nếu lỗi lưu tiến độ, chỉ log ra
                android.util.Log.e("LearningViewModel", "Error submitting review: ${ex.message}")
            }
        }
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
