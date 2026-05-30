package com.example.english_learning_app.data.local

import android.content.Context
import android.content.SharedPreferences

// Quản lý việc lưu trữ JWT Token vào bộ nhớ của điện thoại
class TokenManager(context: Context) {
    
    // Tạo một vùng nhớ kín (chỉ app mình mới đọc được) tên là "minlish_prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences("minlish_prefs", Context.MODE_PRIVATE)

    // Hàm cất token vào tủ
    fun saveToken(token: String) {
        prefs.edit().putString("JWT_TOKEN", token).apply()
    }

    // Hàm lấy token ra xài
    fun getToken(): String? {
        return prefs.getString("JWT_TOKEN", null)
    }

    // Hàm xóa token (dùng khi Đăng xuất)
    fun clearToken() {
        prefs.edit().remove("JWT_TOKEN").apply()
    }
}
