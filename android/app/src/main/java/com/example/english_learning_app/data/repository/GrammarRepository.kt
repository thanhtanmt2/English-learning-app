package com.example.english_learning_app.data.repository

import com.example.english_learning_app.data.local.dao.GrammarNoteDao
import com.example.english_learning_app.data.local.entity.toEntity
import com.example.english_learning_app.data.model.GrammarNote
import com.example.english_learning_app.data.model.QuizQuestion
import com.example.english_learning_app.data.model.QuizScorePayload
import com.example.english_learning_app.data.remote.ApiService

class GrammarRepository(
    private val apiService: ApiService,
    private val grammarNoteDao: GrammarNoteDao? = null
) {

    suspend fun getGrammarNotes(): List<GrammarNote> {
        return try {
            val remote = apiService.getGrammarNotes()
            grammarNoteDao?.upsertAll(remote.map { it.toEntity() })
            remote
        } catch (e: Exception) {
            grammarNoteDao?.getAll()?.map { it.toDomain() } ?: throw e
        }
    }

    suspend fun addGrammarNote(note: GrammarNote): GrammarNote {
        val result = apiService.addGrammarNote(note)
        grammarNoteDao?.upsertAll(listOf(result.toEntity()))
        return result
    }

    suspend fun updateGrammarNote(id: String, note: GrammarNote): GrammarNote {
        val result = apiService.updateGrammarNote(id, note)
        grammarNoteDao?.upsertAll(listOf(result.toEntity()))
        return result
    }

    suspend fun deleteGrammarNote(id: String) {
        apiService.deleteGrammarNote(id)
        grammarNoteDao?.deleteById(id)
    }

    suspend fun getQuizQuestions(noteId: String): List<QuizQuestion> =
        apiService.getGrammarQuizzes(noteId)

    suspend fun submitQuizScore(noteId: String, score: Int, total: Int) =
        apiService.submitGrammarQuizScore(noteId, QuizScorePayload(score, total))
}
