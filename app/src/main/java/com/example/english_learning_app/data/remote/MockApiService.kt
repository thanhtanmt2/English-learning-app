package com.example.english_learning_app.data.remote

import com.example.english_learning_app.data.model.Progress
import com.example.english_learning_app.data.model.User
import com.example.english_learning_app.data.model.Word
import com.example.english_learning_app.data.model.WordPayload
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.model.WordSetPayload
import com.example.english_learning_app.data.model.InAppNotification
import com.example.english_learning_app.data.model.InAppNotificationPayload
import com.example.english_learning_app.data.model.NotificationSettings
import com.example.english_learning_app.data.model.NotificationSettingsPayload
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
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

    @POST("api/v1/wordsets")
    suspend fun createWordSet(
        @Body payload: WordSetPayload
    ): WordSet

    @GET("api/v1/wordsets/{id}")
    suspend fun getWordSet(
        @Path("id") id: String
    ): WordSet

    @PUT("api/v1/wordsets/{id}")
    suspend fun updateWordSet(
        @Path("id") id: String,
        @Body payload: WordSetPayload
    ): WordSet

    @DELETE("api/v1/wordsets/{id}")
    suspend fun deleteWordSet(
        @Path("id") id: String
    )

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

    @GET("api/v1/notifications")
    suspend fun getNotificationSettings(
        @Query("userId") userId: String
    ): List<NotificationSettings>

    @POST("api/v1/notifications")
    suspend fun createNotificationSettings(
        @Body payload: NotificationSettingsPayload
    ): NotificationSettings

    @PATCH("api/v1/notifications/{id}")
    suspend fun updateNotificationSettings(
        @Path("id") id: String,
        @Body payload: NotificationSettingsPayload
    ): NotificationSettings

    @GET("api/v1/in-app-notifications")
    suspend fun getInAppNotifications(
        @Query("userId") userId: String
    ): List<InAppNotification>

    @POST("api/v1/in-app-notifications")
    suspend fun createInAppNotification(
        @Body payload: InAppNotificationPayload
    ): InAppNotification
}
