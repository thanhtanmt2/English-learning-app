package com.example.english_learning_app.data.model

data class WordSet(
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val totalWords: Int,
    val learnedWords: Int,
    val tags: List<String> = emptyList()
)
