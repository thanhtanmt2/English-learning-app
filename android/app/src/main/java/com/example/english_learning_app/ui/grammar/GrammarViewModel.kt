package com.example.english_learning_app.ui.grammar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.GrammarNote
import com.example.english_learning_app.data.model.QuizQuestion
import com.example.english_learning_app.data.repository.GrammarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GrammarUiState(
    val grammarNotes: List<GrammarNote> = emptyList(),
    val quizQuestions: List<QuizQuestion> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isAddSuccess: Boolean = false,
    // Editing state — dùng cho AddEditGrammarScreen
    val editingTitle: String = "",
    val editingCategory: String = "",
    val editingFormula: String = "",
    val editingExplanation: String = "",
    val editingExample: String = "",
    val editingCommonMistakes: String = "",
    val originalNote: GrammarNote? = null,
    val hasUnsavedChanges: Boolean = false,
    val isEditDataLoaded: Boolean = false
)

@HiltViewModel
class GrammarViewModel @Inject constructor(
    private val repository: GrammarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrammarUiState())
    val uiState: StateFlow<GrammarUiState> = _uiState.asStateFlow()

    fun fetchGrammarNotes() {
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }
        viewModelScope.launch {
            try {
                val notes = repository.getGrammarNotes()
                _uiState.update { it.copy(isLoading = false, grammarNotes = notes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi tải ngữ pháp: ${e.message}") }
            }
        }
    }

    fun fetchQuizQuestions(noteId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }
        viewModelScope.launch {
            try {
                val quizzes = repository.getQuizQuestions(noteId)
                _uiState.update { it.copy(isLoading = false, quizQuestions = quizzes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi tải trắc nghiệm: ${e.message}") }
            }
        }
    }

    fun addGrammarNote(
        title: String,
        category: String,
        formula: String,
        explanation: String,
        example: String,
        commonMistakes: String
    ) {
        _uiState.update { it.copy(isLoading = true, errorMessage = "", isAddSuccess = false) }
        viewModelScope.launch {
            try {
                val newNote = GrammarNote(
                    id = "",
                    title = title,
                    category = category,
                    level = "B1",
                    formula = formula,
                    explanation = explanation,
                    example = example,
                    commonMistakes = commonMistakes,
                    tags = emptyList(),
                    easeFactor = 2.5,
                    interval = 0,
                    nextReviewDate = ""
                )
                repository.addGrammarNote(newNote)
                _uiState.update { it.copy(isLoading = false, isAddSuccess = true) }
                fetchGrammarNotes()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi thêm bài học: ${e.message}") }
            }
        }
    }

    fun updateGrammarNote(
        id: String,
        title: String,
        category: String,
        formula: String,
        explanation: String,
        example: String,
        commonMistakes: String
    ) {
        _uiState.update { it.copy(isLoading = true, errorMessage = "", isAddSuccess = false) }
        viewModelScope.launch {
            try {
                val updatedNote = GrammarNote(
                    id = id,
                    title = title,
                    category = category,
                    level = "B1",
                    formula = formula,
                    explanation = explanation,
                    example = example,
                    commonMistakes = commonMistakes,
                    tags = emptyList(),
                    easeFactor = 2.5,
                    interval = 0,
                    nextReviewDate = ""
                )
                repository.updateGrammarNote(id, updatedNote)
                _uiState.update { it.copy(isLoading = false, isAddSuccess = true) }
                fetchGrammarNotes()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi cập nhật: ${e.message}") }
            }
        }
    }

    fun deleteGrammarNote(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteGrammarNote(id)
                fetchGrammarNotes()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi xóa: ${e.message}") }
            }
        }
    }

    fun getGrammarNoteById(id: String): GrammarNote? =
        _uiState.value.grammarNotes.find { it.id == id }

    // Gọi khi mở màn edit: fetch notes nếu chưa có, rồi populate editing fields
    fun loadNoteForEdit(noteId: String) {
        _uiState.update { it.copy(isEditDataLoaded = false) }
        viewModelScope.launch {
            // Fetch mới nhất từ server để đảm bảo dữ liệu không stale
            try {
                val notes = repository.getGrammarNotes()
                _uiState.update { it.copy(grammarNotes = notes) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi tải ngữ pháp: ${e.message}") }
            }
            val note = _uiState.value.grammarNotes.find { it.id == noteId }
            if (note != null) {
                _uiState.update {
                    it.copy(
                        editingTitle = note.title,
                        editingCategory = note.category,
                        editingFormula = note.formula,
                        editingExplanation = note.explanation,
                        editingExample = note.example,
                        editingCommonMistakes = note.commonMistakes,
                        originalNote = note,
                        hasUnsavedChanges = false,
                        isEditDataLoaded = true
                    )
                }
            }
        }
    }

    fun updateEditingTitle(value: String) {
        _uiState.update { it.copy(editingTitle = value, hasUnsavedChanges = computeHasChanges(it.copy(editingTitle = value))) }
    }

    fun updateEditingCategory(value: String) {
        _uiState.update { it.copy(editingCategory = value, hasUnsavedChanges = computeHasChanges(it.copy(editingCategory = value))) }
    }

    fun updateEditingFormula(value: String) {
        _uiState.update { it.copy(editingFormula = value, hasUnsavedChanges = computeHasChanges(it.copy(editingFormula = value))) }
    }

    fun updateEditingExplanation(value: String) {
        _uiState.update { it.copy(editingExplanation = value, hasUnsavedChanges = computeHasChanges(it.copy(editingExplanation = value))) }
    }

    fun updateEditingExample(value: String) {
        _uiState.update { it.copy(editingExample = value, hasUnsavedChanges = computeHasChanges(it.copy(editingExample = value))) }
    }

    fun updateEditingCommonMistakes(value: String) {
        _uiState.update { it.copy(editingCommonMistakes = value, hasUnsavedChanges = computeHasChanges(it.copy(editingCommonMistakes = value))) }
    }

    fun clearEditingState() {
        _uiState.update {
            it.copy(
                editingTitle = "", editingCategory = "", editingFormula = "",
                editingExplanation = "", editingExample = "", editingCommonMistakes = "",
                originalNote = null, hasUnsavedChanges = false, isEditDataLoaded = false
            )
        }
    }

    private fun computeHasChanges(state: GrammarUiState): Boolean {
        val orig = state.originalNote ?: return (
            state.editingTitle.isNotBlank() || state.editingCategory.isNotBlank() ||
            state.editingFormula.isNotBlank() || state.editingExplanation.isNotBlank() ||
            state.editingExample.isNotBlank() || state.editingCommonMistakes.isNotBlank()
        )
        return state.editingTitle != orig.title ||
            state.editingCategory != orig.category ||
            state.editingFormula != orig.formula ||
            state.editingExplanation != orig.explanation ||
            state.editingExample != orig.example ||
            state.editingCommonMistakes != orig.commonMistakes
    }

    fun submitQuizScore(noteId: String, score: Int, total: Int) {
        viewModelScope.launch {
            try {
                repository.submitQuizScore(noteId, score, total)
                fetchGrammarNotes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetAddSuccess() {
        _uiState.update { it.copy(isAddSuccess = false) }
    }
}
