package com.example.english_learning_app.data.remote

import com.example.english_learning_app.data.model.LoginRequest
import com.example.english_learning_app.data.model.RegisterRequest
import com.example.english_learning_app.data.model.User
import retrofit2.http.Body
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

    
    // (Các API cho Grammar và Progress sẽ được thêm vào sau khi chúng ta 
    // code xong UI của phần Auth để bạn đỡ bị ngợp nhé!)
}
