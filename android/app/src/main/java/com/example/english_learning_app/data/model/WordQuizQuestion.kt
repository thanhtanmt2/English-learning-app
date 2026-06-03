package com.example.english_learning_app.data.model

enum class WordQuizDirection {
    EN_TO_VI,
    VI_TO_EN
}

data class WordQuizQuestion(
    val id: String,
    val wordId: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val direction: WordQuizDirection
)
