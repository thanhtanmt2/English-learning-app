package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

// Khuôn dữ liệu để hứng Lịch sử Tiến độ học tập từ mạng về
data class ProgressRecord(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,                  // Của người dùng nào
    @SerializedName("date") val date: String,                      // Ngày học (VD: 2024-05-20)
    @SerializedName("wordsLearned") val wordsLearned: Int,         // Số từ vựng đã học
    @SerializedName("grammarCompleted") val grammarCompleted: Int, // Số bài ngữ pháp đã hoàn thành
    @SerializedName("quizScore") val quizScore: Int,               // Điểm trắc nghiệm đạt được
    @SerializedName("studyTimeMinutes") val studyTimeMinutes: Int  // Số phút đã học trong ngày
)
