package com.example.english_learning_app.data.remote

import com.example.english_learning_app.data.model.Progress
import com.example.english_learning_app.data.model.User
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordPayload
import com.example.english_learning_app.data.model.WordSet
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MockApiService {
    @GET("api/v1/auth/login")
    suspend fun login(
        @Query("email") email: String? = null
    ): List<User>

    @GET("api/v1/wordsets")
    suspend fun getWordSets(
        @Query("userId") userId: String? = null
    ): List<WordSet>

    @GET("api/v1/words")
    suspend fun getWords(
        @Query("wordsetId") wordSetId: String? = null,
        @Query("userId") userId: String? = null
    ): List<Word>

    @GET("api/v1/words/{id}")
    suspend fun getWord(
        @Path("id") id: String
    ): Word

    @POST("api/v1/words")
    suspend fun createWord(
        @Body payload: WordPayload
    ): Word

    @PUT("api/v1/words/{id}")
    suspend fun updateWord(
        @Path("id") id: String,
        @Body payload: WordPayload
    ): Word

    @DELETE("api/v1/words/{id}")
    suspend fun deleteWord(
        @Path("id") id: String
    )

    @GET("api/v1/progress")
    suspend fun getProgress(
        @Query("userId") userId: String
    ): List<Progress>
}
