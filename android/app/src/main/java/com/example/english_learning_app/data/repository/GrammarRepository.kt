package com.example.english_learning_app.data.repository

import com.example.english_learning_app.data.model.GrammarNote
import com.example.english_learning_app.data.model.QuizQuestion
import com.example.english_learning_app.data.remote.ApiService
import com.example.english_learning_app.data.model.QuizScorePayload

class GrammarRepository(private val apiService: ApiService) {

    suspend fun getGrammarNotes(): List<GrammarNote> =
        apiService.getGrammarNotes()

    suspend fun addGrammarNote(note: GrammarNote): GrammarNote =
        apiService.addGrammarNote(note)

    suspend fun updateGrammarNote(id: String, note: GrammarNote): GrammarNote =
        apiService.updateGrammarNote(id, note)

    suspend fun deleteGrammarNote(id: String) =
        apiService.deleteGrammarNote(id)

    suspend fun getQuizQuestions(noteId: String): List<QuizQuestion> =
        apiService.getGrammarQuizzes(noteId)

    suspend fun submitQuizScore(noteId: String, score: Int, total: Int) =
        apiService.submitGrammarQuizScore(noteId, QuizScorePayload(score, total))
}
