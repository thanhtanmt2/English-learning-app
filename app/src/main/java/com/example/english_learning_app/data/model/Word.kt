package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

data class Word(
    val id: Int = 0,
    @SerializedName("word_set_id")
    val wordSetId: Int = 0,
    val word: String,
    val meaning: String,
    val example: String? = null,
    val pronunciation: String? = null,
    @SerializedName("part_of_speech")
    val partOfSpeech: String? = null,
    @SerializedName("ease_factor")
    val easeFactor: Double? = null,
    @SerializedName("next_review_date")
    val nextReviewDate: String? = null
)
