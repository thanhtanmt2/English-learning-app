package com.example.english_learning_app.data.model

data class InAppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val createdAt: String = "",
    val read: Boolean = false
)

data class InAppNotificationPayload(
    val userId: String,
    val title: String,
    val message: String,
    val createdAt: String,
    val read: Boolean
)
