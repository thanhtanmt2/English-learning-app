package com.example.english_learning_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.ProgressRecord
import com.example.english_learning_app.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val streak: Int = 0,
    val accuracyRate: Int = 0,
    val learnedWords: Int = 0,
    val totalWords: Int = 0,
    val reviewToday: Int = 0,
    // retentionRate lấy trực tiếp từ accuracyRate của server (0-100, đã tính đúng)
    val retentionRate: Int = 0,
    val dailyActivity: List<ProgressRecord> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun load() {
        // Guard: không gọi lại nếu đang load hoặc đã có dữ liệu (Bug #2)
        if (_uiState.value.isLoading || _uiState.value.streak > 0 || _uiState.value.learnedWords > 0) return
        fetchData()
    }

    fun forceLoad() {
        if (_uiState.value.isLoading) return
        fetchData()
    }

    private fun fetchData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val overview = apiService.getProgressOverview()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        streak = overview.streak,
                        accuracyRate = overview.accuracyRate,
                        learnedWords = overview.learnedWords,
                        totalWords = overview.totalWords,
                        reviewToday = overview.reviewToday,
                        retentionRate = overview.accuracyRate,
                        dailyActivity = overview.dailyActivity
                    )
                }
            } catch (ex: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = ex.message) }
            }
        }
    }
}
