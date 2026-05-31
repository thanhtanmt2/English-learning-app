package com.example.english_learning_app.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.remote.RetrofitProvider
import com.example.english_learning_app.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WordSetListViewModel : ViewModel() {
    private val repository = VocabularyRepository(RetrofitProvider.apiService)

    private val _uiState = MutableStateFlow(WordSetListUiState())
    val uiState: StateFlow<WordSetListUiState> = _uiState.asStateFlow()

    fun load(force: Boolean = false) {
        if (!force && (_uiState.value.isLoading || _uiState.value.wordSets.isNotEmpty())) {
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val user = repository.loadUser()
                val wordSets = repository.loadWordSets(user.id)
                _uiState.value = WordSetListUiState(isLoading = false, wordSets = wordSets)
            } catch (ex: Exception) {
                _uiState.value = WordSetListUiState(isLoading = false, errorMessage = ex.message)
            }
        }
    }

    fun deleteWordSet(wordSetId: String) {
        viewModelScope.launch {
            try {
                val words = repository.loadWordsByWordSet(wordSetId)
                words.forEach { word -> repository.deleteWord(word.id) }
                repository.deleteWordSet(wordSetId)
                load(force = true)
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = ex.message)
            }
        }
    }
}

data class WordSetListUiState(
    val isLoading: Boolean = false,
    val wordSets: List<WordSet> = emptyList(),
    val errorMessage: String? = null
)
