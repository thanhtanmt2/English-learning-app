package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String,
    @SerializedName("newPassword") val newPassword: String
)
