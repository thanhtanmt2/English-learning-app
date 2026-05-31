package com.example.english_learning_app.data.repository

import com.example.english_learning_app.data.model.User
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordPayload
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.model.WordSetPayload
import com.example.english_learning_app.data.remote.MockApiService

class VocabularyRepository(private val apiService: MockApiService) {
    suspend fun loadUser(): User {
        val users = apiService.login()
        return users.firstOrNull()
            ?: throw IllegalStateException("No users returned from mock API")
    }

    suspend fun loadWordSets(userId: String): List<WordSet> {
        return apiService.getWordSets(userId = userId)
    }

    suspend fun loadWords(userId: String, wordSetId: String? = null): List<Word> {
        return apiService.getWords(wordSetId = wordSetId, userId = userId)
    }

    suspend fun loadAllWords(userId: String): List<Word> {
        return apiService.getWords(userId = userId)
    }

    suspend fun loadWordsByWordSet(wordSetId: String): List<Word> {
        return apiService.getWords(wordSetId = wordSetId)
    }

    suspend fun createWordSet(payload: WordSetPayload): WordSet {
        return apiService.createWordSet(payload)
    }

    suspend fun loadWordSet(wordSetId: String): WordSet {
        return apiService.getWordSet(id = wordSetId)
    }

    suspend fun updateWordSet(wordSetId: String, payload: WordSetPayload): WordSet {
        return apiService.updateWordSet(id = wordSetId, payload = payload)
    }

    suspend fun deleteWordSet(wordSetId: String) {
        apiService.deleteWordSet(id = wordSetId)
    }

    suspend fun loadWord(wordId: String): Word {
        return apiService.getWord(id = wordId)
    }

    suspend fun createWord(payload: WordPayload): Word {
        return apiService.createWord(payload)
    }

    suspend fun updateWord(wordId: String, payload: WordPayload): Word {
        return apiService.updateWord(id = wordId, payload = payload)
    }

    suspend fun deleteWord(wordId: String) {
        apiService.deleteWord(id = wordId)
    }
}
