package com.example.english_learning_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.remote.RetrofitProvider
import com.example.english_learning_app.data.repository.HomeData
import com.example.english_learning_app.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = HomeRepository(com.example.english_learning_app.data.remote.RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                // Thử tải dữ liệu lần 1
                val data = repository.loadHomeData()
                _uiState.value = HomeUiState(isLoading = false, data = data)
            } catch (ex: Exception) {
                // Nếu lỗi 401 (do token chưa kịp nhận), đợi 0.5s rồi thử lại lần cuối
                if (ex.message?.contains("401") == true) {
                    kotlinx.coroutines.delay(500)
                    try {
                        val data = repository.loadHomeData()
                        _uiState.value = HomeUiState(isLoading = false, data = data)
                        return@launch
                    } catch (innerEx: Exception) {
                        _uiState.value = HomeUiState(isLoading = false, errorMessage = innerEx.message)
                    }
                } else {
                    _uiState.value = HomeUiState(isLoading = false, errorMessage = ex.message)
                }
            }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val data: HomeData? = null,
    val errorMessage: String? = null
)
