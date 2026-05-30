package com.example.english_learning_app.data.remote

import com.example.english_learning_app.AppConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitClient đóng vai trò là một cỗ máy tạo ra ApiService.
 * Dùng 'object' trong Kotlin (tương đương Singleton) để đảm bảo toàn bộ app
 * chỉ tạo ra cỗ máy này 1 lần duy nhất, tránh tốn bộ nhớ.
 */
object RetrofitClient {

    // Khởi tạo Retrofit
    // lazy: Có nghĩa là chỉ khi nào biến 'instance' được gọi lần đầu tiên, 
    // thì đoạn code bên trong mới chạy.
    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            // Chỉ định địa chỉ gốc (Ví dụ: http://10.0.2.2:3000/api/v1/)
            .baseUrl(AppConfig.BASE_URL)
            // Khai báo công cụ chuyển đổi JSON -> Kotlin Object (Ở đây dùng Gson)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Biến apiService này là thứ mà các màn hình/ViewModel sẽ gọi ra để xài
    // Ví dụ: RetrofitClient.apiService.login(...)
    val apiService: ApiService by lazy {
        instance.create(ApiService::class.java)
    }
}
