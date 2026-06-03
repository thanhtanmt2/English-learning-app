package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

data class NotificationSettings(
    val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("daily_reminder") val dailyReminder: Boolean = true,
    @SerializedName("reminder_time") val reminderTime: String = "08:00:00",
    @SerializedName("quiz_reminders") val quizReminders: Boolean = true,
    @SerializedName("progress_updates") val progressUpdates: Boolean = true
)
