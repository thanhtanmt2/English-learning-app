package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

data class Word(
    val id: String,
    @SerializedName("wordsetId")
    val wordSetId: String,
    val userId: String,
    val word: String,
    val meaning: String,
    val example: String? = null,
    val pronunciation: String? = null,
    val partOfSpeech: String? = null,
    @SerializedName("ease_factor")
    val easeFactor: Double? = null,
    @SerializedName("next_review_date")
    val nextReviewDate: String? = null
)
