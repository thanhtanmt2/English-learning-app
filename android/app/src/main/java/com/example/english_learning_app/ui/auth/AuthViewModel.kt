package com.example.english_learning_app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.local.TokenManager
import com.example.english_learning_app.data.model.LoginRequest
import com.example.english_learning_app.data.model.RegisterRequest
import com.example.english_learning_app.data.model.ResetPasswordRequest
import com.example.english_learning_app.data.model.UpdateProfilePayload
import com.example.english_learning_app.data.model.User
import com.example.english_learning_app.data.remote.ApiService
import com.example.english_learning_app.R
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
    val successMessage: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val currentUser: User? = null,
    // OTP state
    val otpSent: Boolean = false,
    val otpVerified: Boolean = false,
    val otpEmail: String = "",
    val isPasswordResetSuccess: Boolean = false,
    // Pending register info (kept until OTP complete)
    val pendingGoal: String = "",
    val pendingLevel: String = "",
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
    fun clearLoginSuccess() = _uiState.update { it.copy(isLoginSuccess = false, successMessage = "", errorMessage = "") }
    fun clearRegisterSuccess() = _uiState.update { it.copy(isRegisterSuccess = false, successMessage = "", errorMessage = "") }
    fun clearUpdateSuccess() = _uiState.update { it.copy(isUpdateSuccess = false, successMessage = "") }
    fun clearOtpState() = _uiState.update { it.copy(otpSent = false, otpVerified = false, otpEmail = "", isPasswordResetSuccess = false, errorMessage = "", successMessage = "") }
    fun clearPasswordResetSuccess() = _uiState.update { it.copy(isPasswordResetSuccess = false) }
    fun clearMessages() = _uiState.update { it.copy(errorMessage = "", successMessage = "") }

    fun login() {
        val trimmedEmail = _uiState.value.email.trim()
        val trimmedPassword = _uiState.value.password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_empty_email_password)) }
            return
        }
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_invalid_email)) }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = "", successMessage = "", isLoginSuccess = false) }
        viewModelScope.launch {
            try {
                val authResponse = apiService.login(LoginRequest(trimmedEmail, trimmedPassword))
                tokenManager.saveToken(authResponse.token)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = authResponse.user,
                        successMessage = authResponse.message,
                        errorMessage = "",
                        isLoginSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = parseError(e), successMessage = "") }
            }
        }
    }

    fun loginWithGoogleReal(idToken: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = "", successMessage = "", isLoginSuccess = false) }
        viewModelScope.launch {
            try {
                val response = apiService.googleLogin(mapOf("idToken" to idToken))
                tokenManager.saveToken(response.token)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = response.user,
                        successMessage = getApplication<Application>().getString(R.string.auth_google_login_success, response.user.name),
                        errorMessage = "",
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
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_empty_name_email_password)) }
            return
        }
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_invalid_email)) }
            return
        }
        if (trimmedPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_password_too_short)) }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = "", successMessage = "", isRegisterSuccess = false) }
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
                        successMessage = authResponse.message,
                        errorMessage = "",
                        isRegisterSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = parseError(e), successMessage = "") }
            }
        }
    }

    fun sendRegisterOtp(goal: String, level: String) {
        val trimmedEmail = _uiState.value.email.trim()
        val trimmedName = _uiState.value.name.trim()
        val trimmedPassword = _uiState.value.password.trim()

        if (trimmedName.isBlank() || trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_empty_name_email_password)) }
            return
        }
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_invalid_email)) }
            return
        }
        if (trimmedPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_password_too_short)) }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = "", otpSent = false) }
        viewModelScope.launch {
            try {
                apiService.sendRegisterOtp(mapOf("email" to trimmedEmail))
                _uiState.update {
                    it.copy(isLoading = false, otpSent = true, otpEmail = trimmedEmail,
                        pendingGoal = goal, pendingLevel = level)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = parseError(e)) }
            }
        }
    }

    fun registerComplete(otp: String, goal: String, level: String) {
        val email = _uiState.value.otpEmail
        val name = _uiState.value.name.trim()
        val password = _uiState.value.password.trim()
        _uiState.update { it.copy(isLoading = true, errorMessage = "", isRegisterSuccess = false) }
        viewModelScope.launch {
            try {
                val authResponse = apiService.registerComplete(
                    com.example.english_learning_app.data.model.RegisterRequest(
                        name = name, email = email, password = password, goal = goal, level = level, otp = otp
                    )
                )
                tokenManager.saveToken(authResponse.token)
                _uiState.update {
                    it.copy(isLoading = false, currentUser = authResponse.user, isRegisterSuccess = true, otpSent = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = parseError(e)) }
            }
        }
    }

    fun sendForgotPasswordOtp(email: String) {
        if (!email.contains("@") || !email.contains(".")) {
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_invalid_email)) }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = "", otpSent = false) }
        viewModelScope.launch {
            try {
                apiService.forgotPassword(mapOf("email" to email.trim()))
                _uiState.update { it.copy(isLoading = false, otpSent = true, otpEmail = email.trim()) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = parseError(e)) }
            }
        }
    }

    fun resetPassword(otp: String, newPassword: String) {
        if (newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = getApplication<Application>().getString(R.string.auth_error_password_too_short)) }
            return
        }
        val email = _uiState.value.otpEmail
        _uiState.update { it.copy(isLoading = true, errorMessage = "", isPasswordResetSuccess = false) }
        viewModelScope.launch {
            try {
                apiService.resetPassword(ResetPasswordRequest(email = email, otp = otp, newPassword = newPassword))
                _uiState.update { it.copy(isLoading = false, isPasswordResetSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = parseError(e)) }
            }
        }
    }

    fun updateProfile(newName: String, newGoal: String, newLevel: String) {
        val user = _uiState.value.currentUser ?: return
        _uiState.update { it.copy(isLoading = true, errorMessage = "", successMessage = "", isUpdateSuccess = false) }
        viewModelScope.launch {
            try {
                // Bug #7: gửi payload riêng thay vì cả User object để tránh ghi đè email/id
                val payload = UpdateProfilePayload(name = newName, goal = newGoal, level = newLevel)
                val updatedUser = apiService.updateUser(user.id, payload)
                _uiState.update {
                    it.copy(isLoading = false, currentUser = updatedUser, isUpdateSuccess = true, successMessage = getApplication<Application>().getString(R.string.auth_update_success), errorMessage = "")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = getApplication<Application>().getString(R.string.auth_update_error, e.message), successMessage = "") }
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
