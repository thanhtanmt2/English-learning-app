package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

// Khuôn dữ liệu cho Tổng quan tiến độ (Dashboard)
data class ProgressOverview(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("streak") val streak: Int,
    @SerializedName("totalWordsLearned") val totalWordsLearned: Int,
    @SerializedName("totalWordsMastered") val totalWordsMastered: Int,
    @SerializedName("accuracyRate") val accuracyRate: Int,
    @SerializedName("totalReviews") val totalReviews: Int,
    @SerializedName("totalCorrect") val totalCorrect: Int,
    @SerializedName("level") val level: String,
    @SerializedName("lastStudiedAt") val lastStudiedAt: String
)

// Khuôn dữ liệu cho Lịch sử Tiến độ hàng ngày
data class ProgressRecord(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("date") val date: String,
    @SerializedName("wordsLearned") val wordsLearned: Int,
    @SerializedName("grammarCompleted") val grammarCompleted: Int,
    @SerializedName("quizScore") val quizScore: Int,
    @SerializedName("studyTimeMinutes") val studyTimeMinutes: Int
)
