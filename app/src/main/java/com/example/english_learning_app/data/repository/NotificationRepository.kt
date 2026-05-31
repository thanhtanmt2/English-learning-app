package com.example.english_learning_app.data.repository

import com.example.english_learning_app.data.model.InAppNotification
import com.example.english_learning_app.data.model.InAppNotificationPayload
import com.example.english_learning_app.data.model.NotificationSettings
import com.example.english_learning_app.data.model.NotificationSettingsPayload
import com.example.english_learning_app.data.remote.MockApiService

class NotificationRepository(private val apiService: MockApiService) {
    suspend fun loadNotificationSettings(userId: String): NotificationSettings? {
        return apiService.getNotificationSettings(userId = userId).firstOrNull()
    }

    suspend fun createNotificationSettings(payload: NotificationSettingsPayload): NotificationSettings {
        return apiService.createNotificationSettings(payload)
    }

    suspend fun updateNotificationSettings(
        settingsId: String,
        payload: NotificationSettingsPayload
    ): NotificationSettings {
        return apiService.updateNotificationSettings(id = settingsId, payload = payload)
    }

    suspend fun loadInAppNotifications(userId: String): List<InAppNotification> {
        return apiService.getInAppNotifications(userId = userId)
    }

    suspend fun createInAppNotification(payload: InAppNotificationPayload): InAppNotification {
        return apiService.createInAppNotification(payload)
    }
}
