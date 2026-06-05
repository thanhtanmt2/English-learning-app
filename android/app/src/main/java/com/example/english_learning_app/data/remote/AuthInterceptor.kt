package com.example.english_learning_app.data.remote

import com.example.english_learning_app.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        // Bỏ qua auth routes — không cần token
        if (path.contains("/auth/login") ||
            path.contains("/auth/register") ||
            path.contains("/auth/google") ||
            path.contains("/auth/forgot-password") ||
            path.contains("/auth/reset-password")
        ) {
            return chain.proceed(request)
        }

        val token = tokenManager.getToken()
        if (!token.isNullOrEmpty()) {
            val newRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(request)
    }
}
