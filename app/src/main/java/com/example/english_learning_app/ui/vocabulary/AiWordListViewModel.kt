package com.example.english_learning_app.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.AiGeneratedWord
import com.example.english_learning_app.data.model.AiWordListResult
import com.example.english_learning_app.data.model.WordPayload
import com.example.english_learning_app.data.model.WordSetPayload
import com.example.english_learning_app.data.remote.GeminiService
import com.example.english_learning_app.data.remote.RetrofitProvider
import com.example.english_learning_app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiWordListViewModel : ViewModel() {
    private val repository = VocabularyRepository(RetrofitProvider.apiService)

    private val _uiState = MutableStateFlow(AiWordListUiState())
    val uiState: StateFlow<AiWordListUiState> = _uiState.asStateFlow()

    fun updateTopic(value: String) {
        _uiState.value = _uiState.value.copy(topic = value, errorMessage = null)
    }

    fun updateWordCount(value: Int) {
        _uiState.value = _uiState.value.copy(wordCount = value)
    }

    fun updateResultName(value: String) {
        val result = _uiState.value.result ?: return
        _uiState.value = _uiState.value.copy(result = result.copy(name = value))
    }

    fun updateResultDescription(value: String) {
        val result = _uiState.value.result ?: return
        _uiState.value = _uiState.value.copy(result = result.copy(description = value))
    }

    fun removeWord(index: Int) {
        val result = _uiState.value.result ?: return
        val updatedWords = result.words.toMutableList().also { it.removeAt(index) }
        _uiState.value = _uiState.value.copy(result = result.copy(words = updatedWords))
    }

    fun resetResult() {
        _uiState.value = _uiState.value.copy(result = null, errorMessage = null)
    }

    fun generateWordList() {
        val state = _uiState.value
        if (state.topic.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Vui lòng nhập chủ đề")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isGenerating = true, errorMessage = null, result = null)
            try {
                val result = GeminiService.generateWordList(
                    topic = state.topic.trim(),
                    wordCount = state.wordCount
                )
                _uiState.value = _uiState.value.copy(isGenerating = false, result = result)
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    errorMessage = ex.message ?: "Lỗi không xác định"
                )
            }
        }
    }

    fun saveAll(onSuccess: () -> Unit) {
        val state = _uiState.value
        val result = state.result ?: return
        if (result.words.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "Danh sách từ trống, không thể lưu")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)
            try {
                val user = repository.loadUser()
                val wordSetPayload = WordSetPayload(
                    userId = user.id,
                    name = result.name,
                    description = result.description,
                    tags = result.tags
                )
                val wordSet = repository.createWordSet(wordSetPayload)

                result.words.forEach { aiWord ->
                    val wordPayload = WordPayload(
                        wordsetId = wordSet.id,
                        userId = user.id,
                        word = aiWord.word,
                        meaning = aiWord.meaning,
                        example = aiWord.example.ifBlank { null },
                        pronunciation = aiWord.pronunciation.ifBlank { null },
                        partOfSpeech = aiWord.partOfSpeech.ifBlank { null }
                    )
                    repository.createWord(wordPayload)
                }

                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Lưu thất bại: ${ex.message}"
                )
            }
        }
    }
}

data class AiWordListUiState(
    val topic: String = "",
    val wordCount: Int = 10,
    val isGenerating: Boolean = false,
    val isSaving: Boolean = false,
    val result: AiWordListResult? = null,
    val errorMessage: String? = null
)
