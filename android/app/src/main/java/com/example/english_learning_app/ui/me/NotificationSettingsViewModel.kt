package com.example.english_learning_app.ui.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.NotificationSettings
import com.example.english_learning_app.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationSettingsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val settings: NotificationSettings = NotificationSettings(),
    val statusMessage: String? = null,
    val errorMessage: String? = null
)

class NotificationSettingsViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val settings = repository.getSettings()
                _uiState.update { it.copy(isLoading = false, settings = settings) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateDailyReminder(enabled: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(dailyReminder = enabled)) }
    }

    fun updateReminderTime(time: String) {
        _uiState.update { it.copy(settings = it.settings.copy(reminderTime = time)) }
    }

    fun updateQuizReminders(enabled: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(quizReminders = enabled)) }
    }

    fun updateProgressUpdates(enabled: Boolean) {
        _uiState.update { it.copy(settings = it.settings.copy(progressUpdates = enabled)) }
    }

    fun save() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, statusMessage = null, errorMessage = null) }
            try {
                val updated = repository.updateSettings(_uiState.value.settings)
                _uiState.update { it.copy(isSaving = false, settings = updated, statusMessage = "Đã lưu") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    fun clearStatus() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
