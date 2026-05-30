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
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.PATCH
import retrofit2.http.Path

/**
 * Interface này là nơi liệt kê tất cả các API mà Frontend sẽ gọi.
 * Retrofit sẽ nhìn vào đây để tự động viết code thực thi cho chúng ta.
 */
interface ApiService {

    // API Đăng nhập (Dùng GET để tìm kiếm User trong json-server)
    @GET("users")
    suspend fun login(
        @Query("email") email: String, 
        @Query("password") password: String
    ): List<User>

    // API Đăng ký
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): User

    // Cập nhật User
    @PATCH("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): User

    // Lấy danh sách bài học Ngữ pháp
    @GET("grammar")
    suspend fun getGrammarNotes(): List<GrammarNote>

    // Lấy danh sách câu hỏi trắc nghiệm
    @GET("grammar/quiz")
    suspend fun getGrammarQuizzes(): List<QuizQuestion>

    // Thêm một bài học Ngữ pháp mới
    @POST("grammar")
    suspend fun addGrammarNote(@Body note: GrammarNote): GrammarNote

    // Cập nhật bài học
    @PUT("grammar/{id}")
    suspend fun updateGrammarNote(@Path("id") id: String, @Body note: GrammarNote): GrammarNote

    // Xóa bài học
    @DELETE("grammar/{id}")
    suspend fun deleteGrammarNote(@Path("id") id: String)

    // Lấy danh sách Lịch sử Tiến độ
    @GET("progress")
    suspend fun getProgress(): List<ProgressRecord>
}
