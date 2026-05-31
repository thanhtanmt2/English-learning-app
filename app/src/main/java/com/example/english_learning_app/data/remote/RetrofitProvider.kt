package com.example.english_learning_app.data.remote

import com.example.english_learning_app.AppConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    // Sử dụng chung cấu hình BASE_URL từ AppConfig
    private val BASE_URL = AppConfig.BASE_URL.replace("api/v1/", "")

    val apiService: MockApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MockApiService::class.java)
    }
}
