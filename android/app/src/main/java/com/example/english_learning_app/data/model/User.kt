package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Lớp đại diện cho dữ liệu Người dùng trả về từ API (json-server)
 */
data class User(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("goal") val goal: String?,
    @SerializedName("level") val level: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("createdAt") val createdAt: String?
)
