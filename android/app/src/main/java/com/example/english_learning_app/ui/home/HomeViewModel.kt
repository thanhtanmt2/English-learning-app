package com.example.english_learning_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.repository.HomeData
import com.example.english_learning_app.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val data = repository.loadHomeData()
                _uiState.value = HomeUiState(isLoading = false, data = data)
            } catch (ex: Exception) {
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
