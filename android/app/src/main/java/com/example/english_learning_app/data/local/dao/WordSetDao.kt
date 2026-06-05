package com.example.english_learning_app.data.local.dao

import androidx.room.*
import com.example.english_learning_app.data.local.entity.WordSetEntity

@Dao
interface WordSetDao {
    @Query("SELECT * FROM word_sets")
    suspend fun getAll(): List<WordSetEntity>

    @Upsert
    suspend fun upsertAll(items: List<WordSetEntity>)

    @Query("DELETE FROM word_sets WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM word_sets")
    suspend fun deleteAll()
}
