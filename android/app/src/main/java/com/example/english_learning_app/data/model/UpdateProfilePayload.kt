package com.example.english_learning_app.data.model

// Bug #7: Payload riêng cho PATCH /users/{id} — chỉ gửi các field cần update
// tránh ghi đè email/id/createdAt không mong muốn
data class UpdateProfilePayload(
    val name: String,
    val goal: String,
    val level: String
)
