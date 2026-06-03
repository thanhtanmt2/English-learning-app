package com.example.english_learning_app.data.model

data class WordSetPayload(
    val userId: String,
    val name: String,
    val description: String,
    val tags: List<String> = emptyList(),
    val totalWords: Int = 0,
    val learnedWords: Int = 0
)
