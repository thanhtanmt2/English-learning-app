package com.example.english_learning_app

import com.example.english_learning_app.data.local.dao.PendingReviewDao
import com.example.english_learning_app.data.local.dao.WordDao
import com.example.english_learning_app.data.local.dao.WordSetDao
import com.example.english_learning_app.data.local.entity.PendingReviewEntity
import com.example.english_learning_app.data.local.entity.WordSetEntity
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.remote.ApiService
import com.example.english_learning_app.data.repository.VocabularyRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

class VocabularyRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var wordSetDao: WordSetDao
    private lateinit var wordDao: WordDao
    private lateinit var pendingReviewDao: PendingReviewDao
    private lateinit var repository: VocabularyRepository

    @Before
    fun setUp() {
        apiService = mockk()
        wordSetDao = mockk(relaxed = true)
        wordDao = mockk(relaxed = true)
        pendingReviewDao = mockk(relaxed = true)
        repository = VocabularyRepository(apiService, wordSetDao, wordDao, pendingReviewDao)
    }

    @Test
    fun `loadWordSets thành công trả về dữ liệu từ API và cache vào DB`() = runTest {
        val mockWordSets = listOf(WordSet(id = 1, name = "Test Set", description = null))
        coEvery { apiService.getWordSets() } returns mockWordSets
        coEvery { pendingReviewDao.getAll() } returns emptyList()

        val result = repository.loadWordSets()

        assertEquals(1, result.size)
        assertEquals("Test Set", result[0].name)
        coVerify { wordSetDao.upsertAll(any()) }
    }

    @Test
    fun `loadWordSets fallback về cache khi network lỗi`() = runTest {
        coEvery { apiService.getWordSets() } throws IOException("Network error")
        val cachedEntity = WordSetEntity(
            id = 1, userId = 0, name = "Cached Set",
            description = null, totalWords = 0, learnedWords = 0, tags = ""
        )
        coEvery { wordSetDao.getAll() } returns listOf(cachedEntity)

        val result = repository.loadWordSets()

        assertEquals(1, result.size)
        assertEquals("Cached Set", result[0].name)
    }

    @Test
    fun `submitReview lưu vào pending queue khi offline`() = runTest {
        coEvery { apiService.reviewWord(any(), any()) } throws IOException("Offline")
        coEvery { pendingReviewDao.insert(any()) } just Runs

        repository.submitReview(wordId = 42, quality = 3)

        coVerify {
            pendingReviewDao.insert(match { it.wordId == 42 && it.quality == 3 })
        }
    }

    @Test
    fun `submitReview online không lưu vào pending queue`() = runTest {
        coEvery { apiService.reviewWord(any(), any()) } just Runs

        repository.submitReview(wordId = 5, quality = 5)

        coVerify(exactly = 0) { pendingReviewDao.insert(any()) }
    }

    @Test
    fun `syncPendingReviews xóa record sau khi sync thành công`() = runTest {
        val pending = listOf(PendingReviewEntity(id = 10, wordId = 1, quality = 4))
        coEvery { apiService.getWordSets() } returns emptyList()
        coEvery { pendingReviewDao.getAll() } returns pending
        coEvery { apiService.reviewWord(any(), any()) } just Runs
        coEvery { pendingReviewDao.deleteById(10) } just Runs

        repository.loadWordSets()

        coVerify { pendingReviewDao.deleteById(10) }
    }
}
