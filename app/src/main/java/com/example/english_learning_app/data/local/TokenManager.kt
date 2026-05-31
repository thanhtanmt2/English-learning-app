package com.example.english_learning_app.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Quản lý việc lưu trữ JWT Token.
 *
 * - [cachedToken]: Cache token trong RAM để AuthInterceptor luôn có token
 *   ngay lập tức mà không phụ thuộc vào context hay timing của SharedPreferences.
 * - SharedPreferences dùng để duy trì token giữa các lần mở app.
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("minlish_prefs", Context.MODE_PRIVATE)

    companion object {
        // Token cache trong RAM — AuthInterceptor đọc từ đây trước
        @Volatile
        var cachedToken: String? = null
            private set
    }

    // Lưu token vào cả RAM lẫn SharedPreferences
    fun saveToken(token: String) {
        cachedToken = token
        prefs.edit().putString("JWT_TOKEN", token).apply()
        android.util.Log.d("TokenManager", "Token saved: ${token.take(20)}...")
    }

    // Lấy token: ưu tiên cache RAM, fallback về SharedPreferences
    fun getToken(): String? {
        if (cachedToken != null) return cachedToken
        val stored = prefs.getString("JWT_TOKEN", null)
        if (stored != null) cachedToken = stored  // Load vào cache
        return stored
    }

    // Xóa token khi Đăng xuất
    fun clearToken() {
        cachedToken = null
        prefs.edit().remove("JWT_TOKEN").apply()
    }
}
