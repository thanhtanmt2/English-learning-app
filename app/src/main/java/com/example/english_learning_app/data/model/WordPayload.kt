package com.example.english_learning_app.data.model

data class WordPayload(
    val wordsetId: String,
    val userId: String,
    val word: String,
    val meaning: String,
    val example: String? = null,
    val pronunciation: String? = null,
    val partOfSpeech: String? = null
)
