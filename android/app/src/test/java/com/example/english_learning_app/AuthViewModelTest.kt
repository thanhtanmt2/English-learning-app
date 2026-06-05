package com.example.english_learning_app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.english_learning_app.data.local.TokenManager
import com.example.english_learning_app.data.model.AuthResponse
import com.example.english_learning_app.data.model.User
import com.example.english_learning_app.data.remote.ApiService
import com.example.english_learning_app.ui.auth.AuthViewModel
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
class AuthViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var application: Application
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        application = mockk(relaxed = true)

        // Mock SharedPreferences cho TokenManager (dùng plain prefs vì test không có EncryptedSharedPreferences)
        val sharedPrefs = mockk<SharedPreferences>(relaxed = true)
        val context = mockk<Context>(relaxed = true)
        every { application.applicationContext } returns context
        every { context.getSharedPreferences(any(), any()) } returns sharedPrefs
        every { sharedPrefs.getString(any(), null) } returns null
        mockkConstructor(TokenManager::class)
        every { anyConstructed<TokenManager>().saveToken(any()) } just Runs
        every { anyConstructed<TokenManager>().getToken() } returns null
        every { anyConstructed<TokenManager>().clearToken() } just Runs

        viewModel = AuthViewModel(application, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkConstructor(TokenManager::class)
    }

    @Test
    fun `login với email trống đặt errorMessage`() {
        viewModel.updateEmail("")
        viewModel.updatePassword("password123")

        viewModel.login()

        val state = viewModel.uiState.value
        assertFalse(state.isLoginSuccess)
        assertTrue(state.errorMessage.isNotEmpty())
    }

    @Test
    fun `login với email sai định dạng đặt errorMessage`() {
        viewModel.updateEmail("not-an-email")
        viewModel.updatePassword("password123")

        viewModel.login()

        val state = viewModel.uiState.value
        assertFalse(state.isLoginSuccess)
        assertTrue(state.errorMessage.contains("Email"))
    }

    @Test
    fun `login thành công set isLoginSuccess và currentUser`() = runTest {
        val mockUser = User(id = "1", name = "Bao", email = "bao@test.com", goal = null, level = null, avatarUrl = null, createdAt = null)
        val mockResponse = AuthResponse(message = "OK", token = "jwt_token_abc", user = mockUser)

        coEvery { apiService.login(any()) } returns mockResponse

        viewModel.updateEmail("bao@test.com")
        viewModel.updatePassword("password123")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isLoginSuccess)
        assertEquals(mockUser, state.currentUser)
    }

    @Test
    fun `register với mật khẩu ngắn đặt errorMessage`() {
        viewModel.updateName("Bao")
        viewModel.updateEmail("bao@test.com")
        viewModel.updatePassword("123")

        viewModel.register("Giao tiếp hàng ngày", "A1")

        val state = viewModel.uiState.value
        assertFalse(state.isRegisterSuccess)
        assertTrue(state.errorMessage.contains("6"))
    }
}
