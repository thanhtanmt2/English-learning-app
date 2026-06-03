package com.example.english_learning_app.data.repository

import com.example.english_learning_app.data.model.NotificationSettings
import com.example.english_learning_app.data.remote.ApiService

class NotificationRepository(private val apiService: ApiService) {

    suspend fun getSettings(): NotificationSettings {
        return apiService.getNotificationSettings()
    }

    suspend fun updateSettings(settings: NotificationSettings): NotificationSettings {
        return apiService.updateNotificationSettings(settings)
    }
}
