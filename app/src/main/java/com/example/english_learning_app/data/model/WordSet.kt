package com.example.english_learning_app.data.model

data class WordSet(
    val id: Int = 0,
    val userId: Int = 0,
    val name: String,
    val description: String?,
    val totalWords: Int = 0,
    val learnedWords: Int = 0
)
