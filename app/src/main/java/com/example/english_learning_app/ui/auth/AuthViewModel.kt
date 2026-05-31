package com.example.english_learning_app.ui.auth

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.local.TokenManager
import com.example.english_learning_app.data.model.LoginRequest
import com.example.english_learning_app.data.model.RegisterRequest
import com.example.english_learning_app.data.remote.RetrofitClient
import kotlinx.coroutines.launch

// "Bộ não" xử lý logic cho phần Auth
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)

    // Trạng thái lưu email và password đang gõ
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var name = mutableStateOf("") // Thêm ô nhập Tên cho Đăng ký
    // Trạng thái báo lỗi hoặc thành công để UI hiển thị
    var errorMessage = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var isLoginSuccess = mutableStateOf(false) // Cờ báo hiệu login thành công để chuyển trang
    var isRegisterSuccess = mutableStateOf(false) // Cờ báo hiệu đăng ký thành công
    var isUpdateSuccess = mutableStateOf(false) // Cờ báo hiệu cập nhật profile thành công

    var currentUser = mutableStateOf<com.example.english_learning_app.data.model.User?>(null)

    // Hàm gọi khi user bấm nút "Đăng nhập"
    fun login() {
        val trimmedEmail = email.value.trim()
        val trimmedPassword = password.value.trim()

        // Bắt lỗi cơ bản (Validation)
        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            errorMessage.value = "Vui lòng nhập đầy đủ Email và Mật khẩu!"
            return
        }
        
        // Kiểm tra email đơn giản để tránh lỗi máy ảo
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            errorMessage.value = "Email không đúng định dạng!"
            return
        }

        // Bật trạng thái đang tải
        isLoading.value = true
        errorMessage.value = ""
        isLoginSuccess.value = false

        // Chạy luồng phụ (coroutines) để không đơ UI
        viewModelScope.launch {
            try {
                val authRequest = LoginRequest(trimmedEmail, trimmedPassword)
                val authResponse = RetrofitClient.apiService.login(authRequest)
                
                val user = authResponse.user
                currentUser.value = user
                
                // Lưu token thực từ backend thay vì token giả
                tokenManager.saveToken(authResponse.token)
                
                // Hiển thị thông báo và bật cờ chuyển trang
                errorMessage.value = authResponse.message
                isLoginSuccess.value = true
            } catch (e: Exception) {
                // Hiển thị lỗi chi tiết từ server
                if (e is retrofit2.HttpException) {
                    val rawError = e.response()?.errorBody()?.string()
                    try {
                        val json = com.google.gson.JsonParser.parseString(rawError).asJsonObject
                        errorMessage.value = json.get("message").asString
                    } catch (parseEx: Exception) {
                        errorMessage.value = "Lỗi ${e.code()}: $rawError"
                    }
                } else {
                    errorMessage.value = "Lỗi: ${e.message}"
                }
            } finally {
                // Tắt trạng thái tải
                isLoading.value = false
            }
        }
    }
    // Hàm Đăng ký
    fun register(goal: String, level: String) {
        val trimmedName = name.value.trim()
        val trimmedEmail = email.value.trim()
        val trimmedPassword = password.value.trim()

        // Bắt lỗi Form (Validation) cực kỳ quan trọng cho Đăng ký
        if (trimmedName.isBlank() || trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            errorMessage.value = "Vui lòng nhập đầy đủ Tên, Email và Mật khẩu!"
            return
        }
        
        // Kiểm tra email đơn giản để tránh lỗi máy ảo
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            errorMessage.value = "Email không đúng định dạng!"
            return
        }
        if (trimmedPassword.length < 6) {
            errorMessage.value = "Mật khẩu phải dài từ 6 ký tự trở lên!"
            return
        }

        isLoading.value = true
        errorMessage.value = ""
        isRegisterSuccess.value = false
        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    name = trimmedName,
                    email = trimmedEmail,
                    password = trimmedPassword,
                    goal = goal,
                    level = level
                )
                val authResponse = RetrofitClient.apiService.register(request)
                currentUser.value = authResponse.user
                
                // Lưu token sau khi đăng ký thành công
                tokenManager.saveToken(authResponse.token)
                
                errorMessage.value = authResponse.message
                isRegisterSuccess.value = true
            } catch (e: Exception) {
                // Hiển thị lỗi chi tiết từ server
                if (e is retrofit2.HttpException) {
                    val rawError = e.response()?.errorBody()?.string()
                    try {
                        val json = com.google.gson.JsonParser.parseString(rawError).asJsonObject
                        errorMessage.value = json.get("message").asString
                    } catch (parseEx: Exception) {
                        errorMessage.value = "Lỗi ${e.code()}: $rawError"
                    }
                } else {
                    errorMessage.value = "Lỗi: ${e.message}"
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    // Cập nhật thông tin Profile
    fun updateProfile(newName: String, newGoal: String, newLevel: String) {
        val user = currentUser.value ?: return
        isLoading.value = true
        errorMessage.value = ""
        isUpdateSuccess.value = false

        viewModelScope.launch {
            try {
                val updatedUser = user.copy(name = newName, goal = newGoal, level = newLevel)
                RetrofitClient.apiService.updateUser(user.id, updatedUser)
                currentUser.value = updatedUser
                isUpdateSuccess.value = true
                errorMessage.value = "Cập nhật thành công!"
            } catch (e: Exception) {
                errorMessage.value = "Lỗi cập nhật: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Hàm đăng xuất - xóa token và reset trạng thái
    fun logout() {
        tokenManager.clearToken()
        currentUser.value = null
        email.value = ""
        password.value = ""
        name.value = ""
        errorMessage.value = ""
        isLoginSuccess.value = false
        isRegisterSuccess.value = false
    }
}
