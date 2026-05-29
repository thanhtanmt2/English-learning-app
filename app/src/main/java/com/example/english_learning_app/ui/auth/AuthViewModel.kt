package com.example.english_learning_app.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.LoginRequest
import com.example.english_learning_app.data.remote.RetrofitClient
import kotlinx.coroutines.launch

// "Bộ não" xử lý logic cho phần Auth
class AuthViewModel : ViewModel() {

    // Trạng thái lưu email và password đang gõ
    var email = mutableStateOf("")
    var password = mutableStateOf("")

    // Trạng thái báo lỗi hoặc thành công để UI hiển thị
    var errorMessage = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    // Hàm gọi khi user bấm nút "Đăng nhập"
    fun login() {
        // Bật trạng thái đang tải
        isLoading.value = true
        errorMessage.value = ""

        // Chạy luồng phụ (coroutines) để không đơ UI
        viewModelScope.launch {
            try {
                // Đóng gói dữ liệu gửi đi
                val request = LoginRequest(email.value, password.value)
                
                // Gọi API
                val user = RetrofitClient.apiService.login(request)
                
                // (Sau này sẽ chuyển màn hình ở đây, tạm thời in ra báo thành công)
                errorMessage.value = "Thành công! Xin chào ${user.name}"
                
            } catch (e: Exception) {
                // Bắt lỗi nếu sai pass hoặc sập mạng
                errorMessage.value = "Lỗi: ${e.message}"
            } finally {
                // Tắt trạng thái tải
                isLoading.value = false
            }
        }
    }
}
