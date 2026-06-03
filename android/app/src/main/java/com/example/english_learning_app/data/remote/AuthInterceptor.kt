package com.example.english_learning_app.data.remote

import com.example.english_learning_app.MyApplication
import com.example.english_learning_app.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val path = request.url.encodedPath

        android.util.Log.d("AuthInterceptor", "Request URL: $url")

        // Bỏ qua auth routes — không cần token
        if (path.contains("/auth/login") || 
            path.contains("/auth/register") || 
            path.contains("/auth/google")
        ) {
            return chain.proceed(request)
        }

        // Lấy token từ TokenManager (đã có cơ chế cache bên trong)
        val token = TokenManager(MyApplication.appContext).getToken()

        if (!token.isNullOrEmpty()) {
            android.util.Log.d("AuthInterceptor", "Token found, adding header")
            val newRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }

        android.util.Log.w("AuthInterceptor", "WARNING: No token found! Request to $path will likely return 401.")
        return chain.proceed(request)
    }
}
