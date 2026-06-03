package com.example.english_learning_app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.local.TokenManager
import com.example.english_learning_app.data.model.LoginRequest
import com.example.english_learning_app.data.model.RegisterRequest
import com.example.english_learning_app.data.model.User
import com.example.english_learning_app.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val currentUser: User? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val apiService: ApiService
) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) = _uiState.update { it.copy(email = value) }
    fun updatePassword(value: String) = _uiState.update { it.copy(password = value) }
    fun updateName(value: String) = _uiState.update { it.copy(name = value) }
    fun clearLoginSuccess() = _uiState.update { it.copy(isLoginSuccess = false) }
    fun clearRegisterSuccess() = _uiState.update { it.copy(isRegisterSuccess = false) }
    fun clearUpdateSuccess() = _uiState.update { it.copy(isUpdateSuccess = false) }

    fun login() {
        val trimmedEmail = _uiState.value.email.trim()
        val trimmedPassword = _uiState.value.password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ Email và Mật khẩu!") }
            return
        }
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            _uiState.update { it.copy(errorMessage = "Email không đúng định dạng!") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = "", isLoginSuccess = false) }
        viewModelScope.launch {
            try {
                val authResponse = apiService.login(LoginRequest(trimmedEmail, trimmedPassword))
                tokenManager.saveToken(authResponse.token)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = authResponse.user,
                        errorMessage = authResponse.message,
                        isLoginSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = parseError(e)) }
            }
        }
    }

    fun loginWithGoogleReal(idToken: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = "", isLoginSuccess = false) }
        viewModelScope.launch {
            try {
                val response = apiService.googleLogin(mapOf("idToken" to idToken))
                tokenManager.saveToken(response.token)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = response.user,
                        errorMessage = "Google Login thành công! Xin chào ${response.user.name}",
                        isLoginSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = parseError(e)) }
            }
        }
    }

    fun register(goal: String, level: String) {
        val trimmedName = _uiState.value.name.trim()
        val trimmedEmail = _uiState.value.email.trim()
        val trimmedPassword = _uiState.value.password.trim()

        if (trimmedName.isBlank() || trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ Tên, Email và Mật khẩu!") }
            return
        }
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            _uiState.update { it.copy(errorMessage = "Email không đúng định dạng!") }
            return
        }
        if (trimmedPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu phải dài từ 6 ký tự trở lên!") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = "", isRegisterSuccess = false) }
        viewModelScope.launch {
            try {
                val authResponse = apiService.register(
                    RegisterRequest(name = trimmedName, email = trimmedEmail, password = trimmedPassword, goal = goal, level = level)
                )
                tokenManager.saveToken(authResponse.token)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = authResponse.user,
                        errorMessage = authResponse.message,
                        isRegisterSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = parseError(e)) }
            }
        }
    }

    fun updateProfile(newName: String, newGoal: String, newLevel: String) {
        val user = _uiState.value.currentUser ?: return
        _uiState.update { it.copy(isLoading = true, errorMessage = "", isUpdateSuccess = false) }
        viewModelScope.launch {
            try {
                val updatedUser = user.copy(name = newName, goal = newGoal, level = newLevel)
                apiService.updateUser(user.id, updatedUser)
                _uiState.update {
                    it.copy(isLoading = false, currentUser = updatedUser, isUpdateSuccess = true, errorMessage = "Cập nhật thành công!")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi cập nhật: ${e.message}") }
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
        _uiState.update { AuthUiState() }
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(getApplication())
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseError(e: Exception): String {
        if (e is retrofit2.HttpException) {
            val rawError = e.response()?.errorBody()?.string()
            return try {
                val json = com.google.gson.JsonParser.parseString(rawError).asJsonObject
                json.get("message").asString
            } catch (parseEx: Exception) {
                "Lỗi ${e.code()}: $rawError"
            }
        }
        return "Lỗi: ${e.message}"
    }
}
