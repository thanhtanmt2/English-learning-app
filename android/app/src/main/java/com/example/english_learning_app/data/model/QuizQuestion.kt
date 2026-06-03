package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

// Khuôn dữ liệu chứa câu hỏi trắc nghiệm Ngữ pháp
data class QuizQuestion(
    @SerializedName("id") val id: String,
    @SerializedName("grammarNoteId") val grammarNoteId: String, // Liên kết với bài học nào
    @SerializedName("question") val question: String,           // Nội dung câu hỏi
    @SerializedName("options") val options: List<String>,       // 4 đáp án A, B, C, D
    @SerializedName("correctAnswer") val correctAnswer: String, // Đáp án đúng
    @SerializedName("explanation") val explanation: String,     // Lời giải thích
    @SerializedName("difficulty") val difficulty: String        // Độ khó (easy, medium, hard)
)
