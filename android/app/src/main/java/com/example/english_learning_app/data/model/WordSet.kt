package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

data class WordSet(
    val id: Int = 0,
    @SerializedName("user_id")
    val userId: Int? = null,
    val name: String,
    val description: String?,
    @SerializedName("total_words")
    val totalWords: Int = 0,
    @SerializedName("learned_words")
    val learnedWords: Int = 0,
    @SerializedName("is_default")
    val isDefault: Boolean = false,
    val tags: List<String> = emptyList()
)
