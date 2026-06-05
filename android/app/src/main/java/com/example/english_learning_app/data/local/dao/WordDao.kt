package com.example.english_learning_app.data.local.dao

import androidx.room.*
import com.example.english_learning_app.data.local.entity.WordEntity

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    suspend fun getAll(): List<WordEntity>

    @Query("SELECT * FROM words WHERE wordSetId = :wordSetId")
    suspend fun getByWordSetId(wordSetId: Int): List<WordEntity>

    @Upsert
    suspend fun upsertAll(items: List<WordEntity>)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM words WHERE wordSetId = :wordSetId")
    suspend fun deleteByWordSetId(wordSetId: Int)
}
