package com.example.english_learning_app.data.remote

import com.example.english_learning_app.data.model.GrammarNote
import com.example.english_learning_app.data.model.ProgressOverview
import com.example.english_learning_app.data.model.ProgressRecord
import com.example.english_learning_app.data.model.QuizQuestion
import com.example.english_learning_app.data.model.RegisterRequest
import com.example.english_learning_app.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Query
import retrofit2.http.PATCH
import retrofit2.http.Path as RetrofitPath
import com.example.english_learning_app.data.model.LoginRequest
import com.example.english_learning_app.data.model.AuthResponse
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordPayload
import com.example.english_learning_app.data.model.WordSetPayload

/**
 * Interface này là nơi liệt kê tất cả các API mà Frontend sẽ gọi.
 * Retrofit sẽ nhìn vào đây để tự động viết code thực thi cho chúng ta.
 */
interface ApiService {

    // API Đăng nhập
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // API Đăng ký
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // Cập nhật User
    @PATCH("users/{id}")
    suspend fun updateUser(@RetrofitPath("id") id: String, @Body user: User): User

    // --- VOCABULARY API ---
    
    // Lấy danh sách bộ từ
    @GET("wordsets")
    suspend fun getWordSets(): List<WordSet>
    
    // Lấy thông tin 1 bộ từ
    @GET("wordsets/{id}")
    suspend fun getWordSet(@RetrofitPath("id") id: Int): WordSet
    
    // Tạo bộ từ mới
    @POST("wordsets")
    suspend fun createWordSet(@Body payload: WordSetPayload): WordSet
    
    // Lấy thông tin 1 từ
    @GET("words/{id}")
    suspend fun getWord(@RetrofitPath("id") id: String): Word
    
    // Lấy các từ trong bộ từ
    @GET("wordsets/{id}/words")
    suspend fun getWordsInSet(@RetrofitPath("id") setId: Int): List<Word>
    
    // Thêm từ mới
    @POST("words")
    suspend fun createWord(@Body word: WordPayload): Word
    
    // Cập nhật từ
    @PUT("words/{id}")
    suspend fun updateWord(@RetrofitPath("id") id: Int, @Body word: WordPayload): Word
    
    // Xóa từ
    @DELETE("words/{id}")
    suspend fun deleteWord(@RetrofitPath("id") id: Int)
    
    // Lấy từ ôn tập hôm nay
    @GET("words/review")
    suspend fun getWordsForReview(): List<Word>
    
    // Cập nhật điểm ôn tập (SM-2)
    @POST("words/{id}/review")
    suspend fun reviewWord(@RetrofitPath("id") id: Int, @Body reviewData: Map<String, Int>)

    // Lấy danh sách bài học Ngữ pháp
    @GET("grammar")
    suspend fun getGrammarNotes(): List<GrammarNote>

    // Lấy danh sách câu hỏi trắc nghiệm
    @GET("quiz_questions")
    suspend fun getGrammarQuizzes(): List<QuizQuestion>

    // Thêm một bài học Ngữ pháp mới
    @POST("grammar")
    suspend fun addGrammarNote(@Body note: GrammarNote): GrammarNote

    // Cập nhật bài học
    @PUT("grammar/{id}")
    suspend fun updateGrammarNote(@RetrofitPath("id") id: String, @Body note: GrammarNote): GrammarNote

    // Xóa bài học
    @DELETE("grammar/{id}")
    suspend fun deleteGrammarNote(@RetrofitPath("id") id: String)

    // Lấy danh sách Lịch sử Tiến độ
    @GET("progress_history")
    suspend fun getProgress(): List<ProgressRecord>

    // Lấy Tổng quan tiến độ (Streak, Accuracy...)
    @GET("progress")
    suspend fun getProgressOverview(): ProgressOverview
}
