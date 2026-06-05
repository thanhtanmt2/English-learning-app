package com.example.english_learning_app.data.repository

import com.example.english_learning_app.data.local.dao.PendingReviewDao
import com.example.english_learning_app.data.local.dao.WordDao
import com.example.english_learning_app.data.local.dao.WordSetDao
import com.example.english_learning_app.data.local.entity.PendingReviewEntity
import com.example.english_learning_app.data.local.entity.toEntity
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordPayload
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.model.WordSetPayload
import android.util.Log
import com.example.english_learning_app.data.remote.ApiService

private const val TAG = "VocabRepo"

class VocabularyRepository(
    private val apiService: ApiService,
    private val wordSetDao: WordSetDao? = null,
    private val wordDao: WordDao? = null,
    private val pendingReviewDao: PendingReviewDao? = null
) {

    suspend fun loadWordSets(): List<WordSet> {
        return try {
            val remote = apiService.getWordSets()
            Log.d(TAG, "loadWordSets OK: ${remote.size} sets")
            wordSetDao?.upsertAll(remote.map { it.toEntity() })
            syncPendingReviews()
            remote
        } catch (e: Exception) {
            Log.e(TAG, "loadWordSets FAILED: ${e::class.simpleName}: ${e.message}", e)
            wordSetDao?.getAll()?.map { it.toDomain() } ?: throw e
        }
    }

    suspend fun loadWords(wordSetId: String? = null): List<Word> {
        if (wordSetId != null) {
            val id = wordSetId.toIntOrNull()
                ?: throw IllegalArgumentException("wordSetId không hợp lệ: '$wordSetId'")
            return try {
                val remote = apiService.getWordsInSet(id)
                Log.d(TAG, "loadWords(setId=$id) OK: ${remote.size} words")
                wordDao?.upsertAll(remote.map { it.toEntity() })
                remote
            } catch (e: Exception) {
                Log.e(TAG, "loadWords(setId=$id) FAILED: ${e::class.simpleName}: ${e.message}", e)
                wordDao?.getByWordSetId(id)?.map { it.toDomain() } ?: throw e
            }
        }
        return try {
            val remote = apiService.getWords()
            Log.d(TAG, "loadWords(all) OK: ${remote.size} words")
            wordDao?.upsertAll(remote.map { it.toEntity() })
            remote
        } catch (e: Exception) {
            Log.e(TAG, "loadWords(all) FAILED: ${e::class.simpleName}: ${e.message}", e)
            wordDao?.getAll()?.map { it.toDomain() } ?: throw e
        }
    }

    suspend fun loadAllWords(): List<Word> = loadWords()

    suspend fun loadWordsByWordSet(wordSetId: String): List<Word> = loadWords(wordSetId)

    suspend fun createWordSet(payload: WordSetPayload): WordSet {
        val result = apiService.createWordSet(payload)
        wordSetDao?.upsertAll(listOf(result.toEntity()))
        return result
    }

    suspend fun loadWordSet(wordSetId: String): WordSet {
        val id = wordSetId.toIntOrNull()
            ?: throw IllegalArgumentException("wordSetId không hợp lệ: '$wordSetId'")
        return apiService.getWordSet(id = id)
    }

    suspend fun updateWordSet(wordSetId: String, payload: WordSetPayload): WordSet {
        val id = wordSetId.toIntOrNull()
            ?: throw IllegalArgumentException("wordSetId không hợp lệ: '$wordSetId'")
        val result = apiService.updateWordSet(id = id, payload = payload)
        wordSetDao?.upsertAll(listOf(result.toEntity()))
        return result
    }

    suspend fun deleteWordSet(wordSetId: String) {
        val id = wordSetId.toIntOrNull()
            ?: throw IllegalArgumentException("wordSetId không hợp lệ: '$wordSetId'")
        apiService.deleteWordSet(id = id)
        wordSetDao?.deleteById(id)
        wordDao?.deleteByWordSetId(id)
    }

    suspend fun loadWord(wordId: String): Word = apiService.getWord(id = wordId)

    suspend fun createWord(payload: WordPayload): Word {
        val result = apiService.createWord(payload)
        wordDao?.upsertAll(listOf(result.toEntity()))
        return result
    }

    suspend fun updateWord(wordId: String, payload: WordPayload): Word {
        val result = apiService.updateWord(id = wordId.toIntOrNull() ?: 0, word = payload)
        wordDao?.upsertAll(listOf(result.toEntity()))
        return result
    }

    suspend fun deleteWord(wordId: String) {
        apiService.deleteWord(id = wordId.toIntOrNull() ?: 0)
        wordDao?.deleteById(wordId.toIntOrNull() ?: 0)
    }

    suspend fun submitReview(wordId: Int, quality: Int) {
        try {
            apiService.reviewWord(id = wordId, reviewData = mapOf("quality" to quality))
        } catch (e: Exception) {
            // Lưu vào hàng đợi nếu offline
            pendingReviewDao?.insert(PendingReviewEntity(wordId = wordId, quality = quality))
        }
    }

    // Gửi các review còn pending khi có mạng trở lại
    private suspend fun syncPendingReviews() {
        val pending = pendingReviewDao?.getAll() ?: return
        for (review in pending) {
            try {
                apiService.reviewWord(id = review.wordId, reviewData = mapOf("quality" to review.quality))
                pendingReviewDao.deleteById(review.id)
            } catch (e: Exception) {
                break // Vẫn offline, dừng lại
            }
        }
    }
}
