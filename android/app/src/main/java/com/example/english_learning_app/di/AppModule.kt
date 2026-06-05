package com.example.english_learning_app.di

import android.content.Context
import androidx.room.Room
import com.example.english_learning_app.BuildConfig
import com.example.english_learning_app.data.local.AppDatabase
import com.example.english_learning_app.data.local.ServerPreferences
import com.example.english_learning_app.data.local.TokenManager
import com.example.english_learning_app.data.local.dao.GrammarNoteDao
import com.example.english_learning_app.data.local.dao.PendingReviewDao
import com.example.english_learning_app.data.local.dao.WordDao
import com.example.english_learning_app.data.local.dao.WordSetDao
import com.example.english_learning_app.data.remote.ApiService
import com.example.english_learning_app.data.remote.AuthInterceptor
import com.example.english_learning_app.data.repository.GrammarRepository
import com.example.english_learning_app.data.repository.HomeRepository
import com.example.english_learning_app.data.repository.NotificationRepository
import com.example.english_learning_app.data.repository.VocabularyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): Retrofit {
        val baseUrl = runBlocking {
            context.let {
                ServerPreferences.baseUrlFlow(it).first()
            }
        }.takeIf { it.isNotBlank() } ?: BuildConfig.DEFAULT_BASE_URL
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideVocabularyRepository(
        api: ApiService,
        wordSetDao: WordSetDao,
        wordDao: WordDao,
        pendingReviewDao: PendingReviewDao
    ): VocabularyRepository = VocabularyRepository(api, wordSetDao, wordDao, pendingReviewDao)

    @Provides
    @Singleton
    fun provideHomeRepository(api: ApiService): HomeRepository =
        HomeRepository(api)

    @Provides
    @Singleton
    fun provideGrammarRepository(api: ApiService, grammarNoteDao: GrammarNoteDao): GrammarRepository =
        GrammarRepository(api, grammarNoteDao)

    @Provides
    @Singleton
    fun provideNotificationRepository(api: ApiService): NotificationRepository =
        NotificationRepository(api)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "minlish_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideWordSetDao(db: AppDatabase): WordSetDao = db.wordSetDao()
    @Provides fun provideWordDao(db: AppDatabase): WordDao = db.wordDao()
    @Provides fun provideGrammarNoteDao(db: AppDatabase): GrammarNoteDao = db.grammarNoteDao()
    @Provides fun providePendingReviewDao(db: AppDatabase): PendingReviewDao = db.pendingReviewDao()
}
