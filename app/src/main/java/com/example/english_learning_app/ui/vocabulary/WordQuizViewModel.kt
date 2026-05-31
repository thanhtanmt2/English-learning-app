package com.example.english_learning_app.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordQuizDirection
import com.example.english_learning_app.data.model.WordQuizQuestion
import com.example.english_learning_app.data.remote.RetrofitClient
import com.example.english_learning_app.data.repository.VocabularyRepository
import kotlin.math.min
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WordQuizViewModel : ViewModel() {
    private val repository = VocabularyRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(WordQuizUiState())
    val uiState: StateFlow<WordQuizUiState> = _uiState.asStateFlow()

    private var lastWordSetIds: List<String> =  emptyList()
    private var lastRequestedCount: Int = 0

    fun load(wordSetIds: List<String>, requestedCount: Int) {
        if (_uiState.value.isLoading) {
            return
        }
        lastWordSetIds = wordSetIds
        lastRequestedCount = requestedCount
        _uiState.value = WordQuizUiState(isLoading = true)
        viewModelScope.launch {
            try {
                if (wordSetIds.isEmpty()) {
                    _uiState.value = WordQuizUiState(errorMessage = "Hay chon it nhat 1 word set.")
                    return@launch
                }

                val user = repository.loadUser()
                val allWords = repository.loadAllWords(user.id)
                val selectedWords = allWords.filter { wordSetIds.contains(it.wordSetId.toString()) }

                if (selectedWords.isEmpty()) {
                    _uiState.value = WordQuizUiState(errorMessage = "Khong tim thay tu vung nao trong word set da chon.")
                    return@launch
                }

                val targetCount = requestedCount.coerceIn(5, 100)
                val actualCount = min(targetCount, selectedWords.size)
                val pickedWords = selectedWords.shuffled().take(actualCount)

                val questions = buildQuestions(pickedWords, allWords)
                _uiState.value = WordQuizUiState(
                    isLoading = false,
                    questions = questions,
                    requestedCount = targetCount,
                    actualCount = actualCount,
                    selectedWordCount = selectedWords.size
                )
            } catch (ex: Exception) {
                _uiState.value = WordQuizUiState(errorMessage = ex.message ?: "Khong the tai du lieu.")
            }
        }
    }

    fun reload() {
        load(lastWordSetIds, lastRequestedCount)
    }

    private fun buildQuestions(
        selectedWords: List<Word>,
        allWords: List<Word>
    ): List<WordQuizQuestion> {
        return selectedWords.map { word ->
            val direction = if (Random.nextBoolean()) {
                WordQuizDirection.EN_TO_VI
            } else {
                WordQuizDirection.VI_TO_EN
            }

            val questionText = if (direction == WordQuizDirection.EN_TO_VI) {
                word.word
            } else {
                word.meaning
            }

            val correctAnswer = if (direction == WordQuizDirection.EN_TO_VI) {
                word.meaning
            } else {
                word.word
            }

            val options = buildOptions(direction, correctAnswer, allWords)

            WordQuizQuestion(
                id = "${word.id}-${direction.name}",
                wordId = word.id.toString(),
                question = questionText,
                options = options,
                correctAnswer = correctAnswer,
                direction = direction
            )
        }.shuffled()
    }

    private fun buildOptions(
        direction: WordQuizDirection,
        correctAnswer: String,
        allWords: List<Word>
    ): List<String> {
        val pool = allWords.map { word ->
            if (direction == WordQuizDirection.EN_TO_VI) word.meaning else word.word
        }
            .filter { it.isNotBlank() && it != correctAnswer }
            .distinct()

        val distractors = pool.shuffled().take(3)
        return (distractors + correctAnswer)
            .distinct()
            .shuffled()
    }
}

data class WordQuizUiState(
    val isLoading: Boolean = false,
    val questions: List<WordQuizQuestion> = emptyList(),
    val requestedCount: Int = 0,
    val actualCount: Int = 0,
    val selectedWordCount: Int = 0,
    val errorMessage: String? = null
)
