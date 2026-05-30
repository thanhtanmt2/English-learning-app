package com.example.english_learning_app.data.remote

import com.example.english_learning_app.data.model.GrammarNote
import com.example.english_learning_app.data.model.LoginRequest
import com.example.english_learning_app.data.model.ProgressRecord
import com.example.english_learning_app.data.model.QuizQuestion
import com.example.english_learning_app.data.model.RegisterRequest
import com.example.english_learning_app.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interface này là nơi liệt kê tất cả các API mà Frontend sẽ gọi.
 * Retrofit sẽ nhìn vào đây để tự động viết code thực thi cho chúng ta.
 */
interface ApiService {

    // API Đăng nhập
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): User

    // API Đăng ký
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): User

    // Lấy danh sách bài học Ngữ pháp
    @GET("grammar")
    suspend fun getGrammarNotes(): List<GrammarNote>

    // Lấy danh sách câu hỏi trắc nghiệm
    @GET("grammar/quiz")
    suspend fun getGrammarQuizzes(): List<QuizQuestion>

    // Thêm một bài học Ngữ pháp mới
    @POST("grammar")
    suspend fun addGrammarNote(@Body note: GrammarNote): GrammarNote

    // Lấy danh sách Lịch sử Tiến độ
    @GET("progress")
    suspend fun getProgress(): List<ProgressRecord>
}
