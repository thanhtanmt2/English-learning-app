package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

// Khuôn dữ liệu cho Tổng quan tiến độ (Dashboard)
data class ProgressOverview(
    @SerializedName("streak") val streak: Int,
    @SerializedName("total_words") val totalWords: Int,
    @SerializedName("learned_words") val learnedWords: Int,
    @SerializedName("review_today") val reviewToday: Int,
    @SerializedName("accuracy") val accuracyRate: Int,
    @SerializedName("daily_activity") val dailyActivity: List<ProgressRecord> = emptyList()
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
