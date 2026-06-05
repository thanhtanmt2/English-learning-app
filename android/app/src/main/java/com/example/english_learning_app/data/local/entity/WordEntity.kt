package com.example.english_learning_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.english_learning_app.data.model.Word

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: Int,
    val wordSetId: Int,
    val word: String,
    val meaning: String,
    val example: String?,
    val pronunciation: String?,
    val partOfSpeech: String?,
    val easeFactor: Double?,
    val nextReviewDate: String?,
    val intervalDays: Int?
) {
    fun toDomain(): Word = Word(
        id = id,
        wordSetId = wordSetId,
        word = word,
        meaning = meaning,
        example = example,
        pronunciation = pronunciation,
        partOfSpeech = partOfSpeech,
        easeFactor = easeFactor,
        nextReviewDate = nextReviewDate,
        intervalDays = intervalDays
    )
}

fun Word.toEntity(): WordEntity = WordEntity(
    id = id,
    wordSetId = wordSetId,
    word = word,
    meaning = meaning,
    example = example,
    pronunciation = pronunciation,
    partOfSpeech = partOfSpeech,
    easeFactor = easeFactor,
    nextReviewDate = nextReviewDate,
    intervalDays = intervalDays
)
