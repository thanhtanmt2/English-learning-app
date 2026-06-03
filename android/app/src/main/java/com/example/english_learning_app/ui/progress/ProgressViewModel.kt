package com.example.english_learning_app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.ProgressOverview
import com.example.english_learning_app.data.model.ProgressRecord
import com.example.english_learning_app.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressUiState(
    val progressOverview: ProgressOverview? = null,
    val progressRecords: List<ProgressRecord> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    fun fetchProgress() {
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }
        viewModelScope.launch {
            try {
                val overview = apiService.getProgressOverview()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        progressOverview = overview,
                        progressRecords = overview.dailyActivity
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi tải tiến độ: ${e.message}") }
            }
        }
    }
}
