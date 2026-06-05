package com.example.english_learning_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.english_learning_app.data.local.dao.GrammarNoteDao
import com.example.english_learning_app.data.local.dao.PendingReviewDao
import com.example.english_learning_app.data.local.dao.WordDao
import com.example.english_learning_app.data.local.dao.WordSetDao
import com.example.english_learning_app.data.local.entity.GrammarNoteEntity
import com.example.english_learning_app.data.local.entity.PendingReviewEntity
import com.example.english_learning_app.data.local.entity.WordEntity
import com.example.english_learning_app.data.local.entity.WordSetEntity

@Database(
    entities = [
        WordSetEntity::class,
        WordEntity::class,
        GrammarNoteEntity::class,
        PendingReviewEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordSetDao(): WordSetDao
    abstract fun wordDao(): WordDao
    abstract fun grammarNoteDao(): GrammarNoteDao
    abstract fun pendingReviewDao(): PendingReviewDao
}
