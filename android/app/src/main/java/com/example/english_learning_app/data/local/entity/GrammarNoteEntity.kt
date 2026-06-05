package com.example.english_learning_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.english_learning_app.data.model.GrammarNote

@Entity(tableName = "grammar_notes")
data class GrammarNoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val level: String,
    val formula: String,
    val explanation: String,
    val example: String,
    val commonMistakes: String,
    val tags: String, // comma-separated
    val easeFactor: Double?,
    val interval: Int?,
    val nextReviewDate: String?,
    val highestScore: Int?,
    val totalQuestions: Int?
) {
    fun toDomain(): GrammarNote = GrammarNote(
        id = id,
        title = title,
        category = category,
        level = level,
        formula = formula,
        explanation = explanation,
        example = example,
        commonMistakes = commonMistakes,
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        easeFactor = easeFactor,
        interval = interval,
        nextReviewDate = nextReviewDate,
        highestScore = highestScore,
        totalQuestions = totalQuestions
    )
}

fun GrammarNote.toEntity(): GrammarNoteEntity = GrammarNoteEntity(
    id = id,
    title = title,
    category = category,
    level = level,
    formula = formula,
    explanation = explanation,
    example = example,
    commonMistakes = commonMistakes,
    tags = tags?.joinToString(",") ?: "",
    easeFactor = easeFactor,
    interval = interval,
    nextReviewDate = nextReviewDate,
    highestScore = highestScore,
    totalQuestions = totalQuestions
)
