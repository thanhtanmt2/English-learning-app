package com.example.english_learning_app.data.model

data class Progress(
    val id: String,
    val userId: String,
    val streak: Int,
    val totalWordsLearned: Int,
    val totalWordsMastered: Int,
    val accuracyRate: Int,
    val level: String,
    val dailyPlan: DailyPlan
)

data class DailyPlan(
    val newWordsTarget: Int,
    val reviewWordsTarget: Int,
    val newWordsDone: Int,
    val reviewWordsDone: Int
)
