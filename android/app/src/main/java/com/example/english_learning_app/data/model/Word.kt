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
    val nextReviewDate: String? = null,
    @SerializedName("interval_days")
    val intervalDays: Int? = null
) {
    // Từ được coi là "đã học" khi interval_days > 1 (đã ôn tập ít nhất 1 lần thành công theo SM-2)
    val isLearned: Boolean get() = (intervalDays ?: 1) > 1
}
