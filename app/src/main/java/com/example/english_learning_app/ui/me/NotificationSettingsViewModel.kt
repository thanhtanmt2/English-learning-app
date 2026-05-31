package com.example.english_learning_app.ui.me

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.InAppNotification
import com.example.english_learning_app.data.model.InAppNotificationPayload
import com.example.english_learning_app.data.model.NotificationSettings
import com.example.english_learning_app.data.model.NotificationSettingsPayload
import com.example.english_learning_app.data.remote.RetrofitProvider
import com.example.english_learning_app.data.repository.NotificationRepository
import java.time.Instant
import kotlinx.coroutines.launch

data class NotificationSettingsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val settings: NotificationSettings = NotificationSettings(),
    val inAppNotifications: List<InAppNotification> = emptyList(),
    val statusMessage: String? = null,
    val errorMessage: String? = null
)

class NotificationSettingsViewModel : ViewModel() {
    private val repository = NotificationRepository(RetrofitProvider.apiService)

    var uiState by mutableStateOf(NotificationSettingsUiState())
        private set

    fun load(userId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val settings = repository.loadNotificationSettings(userId)
                    ?: repository.createNotificationSettings(buildPayload(NotificationSettings(userId = userId)))
                val notifications = repository.loadInAppNotifications(userId)
                uiState = uiState.copy(
                    isLoading = false,
                    settings = settings,
                    inAppNotifications = notifications
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun updateDailyReminderEnabled(enabled: Boolean) {
        updateSettings { copy(dailyReminderEnabled = enabled) }
    }

    fun updateDailyReminderTime(time: String) {
        updateSettings { copy(dailyReminderTime = time) }
    }

    fun updateReviewReminderEnabled(enabled: Boolean) {
        updateSettings { copy(reviewReminderEnabled = enabled) }
    }

    fun updateStreakReminderEnabled(enabled: Boolean) {
        updateSettings { copy(streakReminderEnabled = enabled) }
    }

    fun updatePushEnabled(enabled: Boolean) {
        updateSettings { copy(pushNotificationEnabled = enabled) }
    }

    fun updateEmailEnabled(enabled: Boolean) {
        updateSettings { copy(emailNotificationEnabled = enabled) }
    }

    fun updateDailyMessage(message: String) {
        updateSettings { copy(dailyReminderMessage = message) }
    }

    fun updateReviewMessage(message: String) {
        updateSettings { copy(reviewReminderMessage = message) }
    }

    fun updateStreakMessage(message: String) {
        updateSettings { copy(streakReminderMessage = message) }
    }

    fun updateInAppTitle(title: String) {
        updateSettings { copy(inAppTitle = title) }
    }

    fun updateInAppMessage(message: String) {
        updateSettings { copy(inAppMessage = message) }
    }

    fun updateEmailSubject(subject: String) {
        updateSettings { copy(emailSubject = subject) }
    }

    fun updateEmailMessage(message: String) {
        updateSettings { copy(emailMessage = message) }
    }

    fun save() {
        val settings = uiState.settings
        if (settings.userId.isBlank()) {
            uiState = uiState.copy(errorMessage = "Missing user")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, statusMessage = null, errorMessage = null)
            try {
                val payload = buildPayload(settings)
                val updated = if (settings.id.isBlank()) {
                    repository.createNotificationSettings(payload)
                } else {
                    repository.updateNotificationSettings(settings.id, payload)
                }
                uiState = uiState.copy(
                    isSaving = false,
                    settings = updated,
                    statusMessage = "Saved"
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, errorMessage = e.message)
            }
        }
    }

    fun sendTestInApp() {
        val settings = uiState.settings
        if (settings.userId.isBlank()) {
            uiState = uiState.copy(errorMessage = "Missing user")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, statusMessage = null, errorMessage = null)
            try {
                val payload = InAppNotificationPayload(
                    userId = settings.userId,
                    title = settings.inAppTitle.ifBlank { "Thong bao" },
                    message = settings.inAppMessage.ifBlank { "Nhan thong bao test" },
                    createdAt = Instant.now().toString(),
                    read = false
                )
                val created = repository.createInAppNotification(payload)
                val updatedList = listOf(created) + uiState.inAppNotifications
                uiState = uiState.copy(
                    isSaving = false,
                    inAppNotifications = updatedList,
                    statusMessage = "In-app sent"
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isSaving = false, errorMessage = e.message)
            }
        }
    }

    fun clearStatus() {
        uiState = uiState.copy(statusMessage = null)
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    private fun updateSettings(update: NotificationSettings.() -> NotificationSettings) {
        uiState = uiState.copy(settings = uiState.settings.update())
    }

    private fun buildPayload(settings: NotificationSettings): NotificationSettingsPayload {
        return NotificationSettingsPayload(
            userId = settings.userId,
            dailyReminderEnabled = settings.dailyReminderEnabled,
            dailyReminderTime = settings.dailyReminderTime,
            reviewReminderEnabled = settings.reviewReminderEnabled,
            pushNotificationEnabled = settings.pushNotificationEnabled,
            emailNotificationEnabled = settings.emailNotificationEnabled,
            streakReminderEnabled = settings.streakReminderEnabled,
            dailyReminderMessage = settings.dailyReminderMessage,
            reviewReminderMessage = settings.reviewReminderMessage,
            streakReminderMessage = settings.streakReminderMessage,
            inAppTitle = settings.inAppTitle,
            inAppMessage = settings.inAppMessage,
            emailSubject = settings.emailSubject,
            emailMessage = settings.emailMessage,
            updatedAt = Instant.now().toString()
        )
    }
}
