package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Phản hồi từ server khi Đăng nhập hoặc Đăng ký thành công
 */
data class AuthResponse(
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: User
)
