package com.example.english_learning_app.data.model

data class WordPayload(
    @com.google.gson.annotations.SerializedName("word_set_id")
    val wordSetId: Int,
    val word: String,
    val meaning: String,
    val example: String? = null,
    val pronunciation: String? = null,
    @com.google.gson.annotations.SerializedName("part_of_speech")
    val partOfSpeech: String? = null
)
