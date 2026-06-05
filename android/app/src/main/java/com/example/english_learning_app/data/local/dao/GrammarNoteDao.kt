package com.example.english_learning_app.data.local.dao

import androidx.room.*
import com.example.english_learning_app.data.local.entity.GrammarNoteEntity

@Dao
interface GrammarNoteDao {
    @Query("SELECT * FROM grammar_notes")
    suspend fun getAll(): List<GrammarNoteEntity>

    @Upsert
    suspend fun upsertAll(items: List<GrammarNoteEntity>)

    @Query("DELETE FROM grammar_notes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM grammar_notes")
    suspend fun deleteAll()
}
