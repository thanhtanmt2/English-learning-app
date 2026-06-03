package com.example.english_learning_app.data.model

data class AiGeneratedWord(
    val word: String,
    val meaning: String,
    val example: String = "",
    val pronunciation: String = "",
    val partOfSpeech: String = ""
)

data class AiWordListResult(
    val name: String,
    val description: String,
    val tags: List<String>,
    val words: List<AiGeneratedWord>
)
