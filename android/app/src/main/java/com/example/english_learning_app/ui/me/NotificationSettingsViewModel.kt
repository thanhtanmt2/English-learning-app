package com.example.english_learning_app.ui.me

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.english_learning_app.data.model.NotificationSettings
import com.example.english_learning_app.data.repository.NotificationRepository
import com.example.english_learning_app.ui.utils.NotificationHelper
import com.example.english_learning_app.workers.DailyReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationSettingsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val settings: NotificationSettings = NotificationSettings(),
    val statusMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    application: Application,
    private val repository: NotificationRepository
) : AndroidViewModel(application) {

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

                val ctx = getApplication<Application>()
                if (updated.dailyReminder) {
                    NotificationHelper.scheduleReminder(ctx, updated.reminderTime)
                } else {
                    NotificationHelper.cancelReminder(ctx)
                }
                if (updated.quizReminders) {
                    NotificationHelper.scheduleQuizReminder(ctx)
                } else {
                    NotificationHelper.cancelQuizReminder(ctx)
                }
                if (updated.progressUpdates) {
                    NotificationHelper.scheduleProgressUpdate(ctx)
                } else {
                    NotificationHelper.cancelProgressUpdate(ctx)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    // Bắn thông báo thử ngay lập tức — không cần đợi đến giờ hẹn
    // Dùng OneTimeWorkRequest: chạy Worker đúng 1 lần, delay = 0
    fun testNotification() {
        val ctx = getApplication<Application>()
        val testRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>().build()
        WorkManager.getInstance(ctx).enqueue(testRequest)
        _uiState.update { it.copy(statusMessage = "Đang gửi thông báo thử...") }
    }

    fun clearStatus() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
