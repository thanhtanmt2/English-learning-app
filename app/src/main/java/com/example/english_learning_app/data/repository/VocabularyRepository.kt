package com.example.english_learning_app.data.repository

import com.example.english_learning_app.data.model.User
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordPayload
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.model.WordSetPayload
import com.example.english_learning_app.data.remote.ApiService

class VocabularyRepository(private val apiService: ApiService) {
    suspend fun loadUser(): User {
        return User(id = "0", name = "", email = "", goal = "", level = "", avatarUrl = null, createdAt = null)
    }

    suspend fun loadWordSets(userId: String): List<WordSet> {
        return apiService.getWordSets()
    }

    suspend fun loadWords(userId: String, wordSetId: String): List<Word> {
        return apiService.getWordsInSet(wordSetId.toIntOrNull() ?: 0)
    }

    suspend fun createWordSet(payload: WordSetPayload): WordSet {
        return apiService.createWordSet(payload)
    }

    suspend fun loadWord(wordId: String): Word {
        return apiService.getWord(id = wordId)
    }

    suspend fun createWord(payload: WordPayload): Word {
        return apiService.createWord(payload)
    }

    suspend fun updateWord(wordId: String, payload: WordPayload): Word {
        return apiService.updateWord(id = wordId.toIntOrNull() ?: 0, word = payload)
    }

    suspend fun deleteWord(wordId: String) {
        apiService.deleteWord(id = wordId.toIntOrNull() ?: 0)
    }

    suspend fun submitReview(wordId: Int, quality: Int) {
        apiService.reviewWord(id = wordId, reviewData = mapOf("quality" to quality))
    }
}
