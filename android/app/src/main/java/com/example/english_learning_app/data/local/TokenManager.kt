package com.example.english_learning_app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Quản lý việc lưu trữ JWT Token.
 *
 * - [cachedToken]: Cache token trong RAM để AuthInterceptor luôn có token
 *   ngay lập tức mà không phụ thuộc vào context hay timing của SharedPreferences.
 * - EncryptedSharedPreferences dùng để duy trì token giữa các lần mở app (AES256).
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences by lazy {
        val appContext = context.applicationContext
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            appContext,
            "minlish_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        // Token cache trong RAM — AuthInterceptor đọc từ đây trước
        @Volatile
        var cachedToken: String? = null
            private set
    }

    fun saveToken(token: String) {
        cachedToken = token
        prefs.edit().putString("JWT_TOKEN", token).apply()
    }

    fun getToken(): String? {
        if (cachedToken != null) return cachedToken
        val stored = prefs.getString("JWT_TOKEN", null)
        if (stored != null) cachedToken = stored
        return stored
    }

    fun clearToken() {
        cachedToken = null
        prefs.edit().remove("JWT_TOKEN").apply()
    }
}
