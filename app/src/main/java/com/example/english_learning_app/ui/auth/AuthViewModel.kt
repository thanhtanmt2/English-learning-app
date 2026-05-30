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

    // Hàm gọi khi user bấm nút "Đăng nhập"
    fun login() {
        // Bắt lỗi cơ bản (Validation)
        if (email.value.isBlank() || password.value.isBlank()) {
            errorMessage.value = "Vui lòng nhập đầy đủ Email và Mật khẩu!"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
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
                // Gọi lên mạng để tìm kiếm User
                val users = RetrofitClient.apiService.login(email.value, password.value)
                
                if (users.isNotEmpty()) {
                    val user = users[0] // Lấy người đầu tiên tìm thấy
                    // Lưu token vào TokenManager
                    tokenManager.saveToken("fake_jwt_token_for_user_${user.id}")
                    
                    // Hiển thị thông báo và bật cờ chuyển trang
                    errorMessage.value = "Thành công! Xin chào ${user.name}"
                    isLoginSuccess.value = true
                } else {
                    // Trả về danh sách rỗng tức là không tìm thấy
                    errorMessage.value = "Sai Email hoặc Mật khẩu!"
                }
            } catch (e: Exception) {
                // Bắt lỗi nếu sai pass hoặc sập mạng
                errorMessage.value = "Lỗi: ${e.message}"
            } finally {
                // Tắt trạng thái tải
                isLoading.value = false
            }
        }
    }
    // Hàm Đăng ký
    fun register(goal: String, level: String) {
        // Bắt lỗi Form (Validation) cực kỳ quan trọng cho Đăng ký
        if (name.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
            errorMessage.value = "Vui lòng nhập đầy đủ Tên, Email và Mật khẩu!"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            errorMessage.value = "Email không đúng định dạng!"
            return
        }
        if (password.value.length < 6) {
            errorMessage.value = "Mật khẩu phải dài từ 6 ký tự trở lên!"
            return
        }

        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                // Đóng gói dữ liệu gửi đi với đầy đủ 5 thông tin
                val request = RegisterRequest(
                    name = name.value,
                    email = email.value,
                    password = password.value,
                    goal = goal,
                    level = level
                )
                val user = RetrofitClient.apiService.register(request)
                
                // Lưu token sau khi đăng ký thành công
                tokenManager.saveToken("fake_jwt_token_for_user_${user.id}")
                
                errorMessage.value = "Đăng ký thành công! Chào ${user.name}"
            } catch (e: Exception) {
                errorMessage.value = "Lỗi: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
