package com.example.english_learning_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.english_learning_app.data.model.WordSet

@Entity(tableName = "word_sets")
data class WordSetEntity(
    @PrimaryKey val id: Int,
    val userId: Int?,
    val name: String,
    val description: String?,
    val totalWords: Int,
    val learnedWords: Int,
    val isDefault: Boolean = false,
    val tags: String
) {
    fun toDomain(): WordSet = WordSet(
        id = id,
        userId = userId,
        name = name,
        description = description,
        totalWords = totalWords,
        learnedWords = learnedWords,
        isDefault = isDefault,
        tags = if (tags.isNullOrBlank()) emptyList() else tags.split(",")
    )
}

fun WordSet.toEntity(): WordSetEntity = WordSetEntity(
    id = id,
    userId = userId,
    name = name,
    description = description,
    totalWords = totalWords,
    learnedWords = learnedWords,
    isDefault = isDefault,
    tags = tags?.joinToString(",") ?: ""
)
