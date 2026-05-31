package com.example.english_learning_app.data.remote

import com.example.english_learning_app.MyApplication
import com.example.english_learning_app.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // Sử dụng request.url() thay vì request.url để tránh lỗi package-private access trong một số cấu hình Kotlin/OkHttp
        val url = request.url().toString()

        android.util.Log.d("AuthInterceptor", "Request URL: $url")

        // Bỏ qua auth routes — không cần token
        if (url.contains("/auth/login") || url.contains("/auth/register")) {
            return chain.proceed(request)
        }

        // Ưu tiên lấy từ cachedToken (trong RAM), fallback về SharedPreferences
        val token = TokenManager.cachedToken
            ?: TokenManager(MyApplication.appContext).getToken()

        if (!token.isNullOrEmpty()) {
            android.util.Log.d("AuthInterceptor", "Token found, adding header")
            val newRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }

        android.util.Log.w("AuthInterceptor", "WARNING: No token found! Request will likely return 401.")
        return chain.proceed(request)
    }
}
