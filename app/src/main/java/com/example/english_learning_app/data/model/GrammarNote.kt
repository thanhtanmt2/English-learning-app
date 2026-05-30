package com.example.english_learning_app.data.model

import com.google.gson.annotations.SerializedName

// Khuôn dữ liệu để hứng bài học Ngữ pháp từ mạng về
data class GrammarNote(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,               // Tên bài học (VD: Hiện tại hoàn thành)
    @SerializedName("category") val category: String,         // Thể loại (Thì động từ, Câu điều kiện...)
    @SerializedName("formula") val formula: String,           // Công thức
    @SerializedName("explanation") val explanation: String,   // Giải thích chi tiết
    @SerializedName("example") val example: String,           // Câu ví dụ
    @SerializedName("commonMistakes") val commonMistakes: String, // Lỗi sai hay gặp
    @SerializedName("tags") val tags: List<String>?,          // Các thẻ phân loại (IELTS, TOEIC...)
    @SerializedName("ease_factor") val easeFactor: Double?,   // Biến số thuật toán gợi ý ôn tập
    @SerializedName("interval") val interval: Int?,           // Số ngày tới lần ôn tiếp theo
    @SerializedName("next_review_date") val nextReviewDate: String?
)
