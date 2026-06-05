package com.example.english_learning_app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.repository.VocabularyRepository
import com.example.english_learning_app.ui.learning.FlashcardUiState
import com.example.english_learning_app.ui.learning.FlashcardViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlashcardViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: VocabularyRepository
    private lateinit var viewModel: FlashcardViewModel

    private val testWord = Word(id = 1, wordSetId = 1, word = "hello", meaning = "xin chào", intervalDays = 0)
    private val testWordSet = WordSet(id = 1, name = "Test Set", description = null)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = FlashcardViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitReview gọi repository với quality đúng`() = runTest {
        coEvery { repository.loadWordSets() } returns listOf(testWordSet)
        coEvery { repository.loadWords(wordSetId = "1") } returns listOf(testWord)
        coEvery { repository.submitReview(any(), any()) } just Runs

        viewModel.load()
        viewModel.selectWordSet(testWordSet)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.submitReview(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.submitReview(1, 1) }
    }

    @Test
    fun `submitReview quality gte 3 cap nhat optimistic intervalDays`() = runTest {
        coEvery { repository.loadWordSets() } returns listOf(testWordSet)
        coEvery { repository.loadWords(wordSetId = "1") } returns listOf(testWord)
        coEvery { repository.submitReview(any(), any()) } just Runs

        viewModel.load()
        viewModel.selectWordSet(testWordSet)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.submitReview(3)
        testDispatcher.scheduler.advanceUntilIdle()

        val word = viewModel.uiState.value.words.firstOrNull { it.id == 1 }
        assertNotNull(word)
        assertTrue("intervalDays nên > 1 sau quality >= 3", (word?.intervalDays ?: 0) > 1)
    }

    @Test
    fun `submitReview quality 1 không cập nhật intervalDays`() = runTest {
        coEvery { repository.loadWordSets() } returns listOf(testWordSet)
        coEvery { repository.loadWords(wordSetId = "1") } returns listOf(testWord)
        coEvery { repository.submitReview(any(), any()) } just Runs

        viewModel.load()
        viewModel.selectWordSet(testWordSet)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.submitReview(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val word = viewModel.uiState.value.words.firstOrNull { it.id == 1 }
        // Quality < 3 không optimistic update
        assertEquals(0, word?.intervalDays ?: 0)
    }
}
