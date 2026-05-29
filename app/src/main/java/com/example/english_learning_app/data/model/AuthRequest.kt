package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Lớp chứa dữ liệu gửi lên API khi Đăng nhập
 */
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

/**
 * Lớp chứa dữ liệu gửi lên API khi Đăng ký
 */
data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String
)
