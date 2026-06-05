package com.example.english_learning_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_reviews")
data class PendingReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wordId: Int,
    val quality: Int,
    val createdAt: Long = System.currentTimeMillis()
)
