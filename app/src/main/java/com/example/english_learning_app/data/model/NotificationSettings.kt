package com.example.english_learning_app.data.model

data class NotificationSettings(
    val id: String = "",
    val userId: String = "",
    val dailyReminderEnabled: Boolean = true,
    val dailyReminderTime: String = "08:00",
    val reviewReminderEnabled: Boolean = false,
    val pushNotificationEnabled: Boolean = true,
    val emailNotificationEnabled: Boolean = false,
    val streakReminderEnabled: Boolean = false,
    val dailyReminderMessage: String = "Hoc tap moi ngay nhe!",
    val reviewReminderMessage: String = "Den gio on tap roi!",
    val streakReminderMessage: String = "Giu chuoi hoc tap nhe!",
    val inAppTitle: String = "Thong bao hoc tap",
    val inAppMessage: String = "Hom nay hoc 10 tu moi nhe!",
    val emailSubject: String = "Nhac hoc tieng Anh",
    val emailMessage: String = "Hom nay ban hay hoc them tu vung de giu thoi quen.",
    val updatedAt: String? = null
)

data class NotificationSettingsPayload(
    val userId: String,
    val dailyReminderEnabled: Boolean,
    val dailyReminderTime: String,
    val reviewReminderEnabled: Boolean,
    val pushNotificationEnabled: Boolean,
    val emailNotificationEnabled: Boolean,
    val streakReminderEnabled: Boolean,
    val dailyReminderMessage: String,
    val reviewReminderMessage: String,
    val streakReminderMessage: String,
    val inAppTitle: String,
    val inAppMessage: String,
    val emailSubject: String,
    val emailMessage: String,
    val updatedAt: String?
)
