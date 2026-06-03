package com.example.english_learning_app.di

import com.example.english_learning_app.data.remote.ApiService
import com.example.english_learning_app.data.remote.RetrofitClient
import com.example.english_learning_app.data.repository.GrammarRepository
import com.example.english_learning_app.data.repository.HomeRepository
import com.example.english_learning_app.data.repository.NotificationRepository
import com.example.english_learning_app.data.repository.VocabularyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService = RetrofitClient.apiService

    @Provides
    @Singleton
    fun provideVocabularyRepository(api: ApiService): VocabularyRepository =
        VocabularyRepository(api)

    @Provides
    @Singleton
    fun provideHomeRepository(api: ApiService): HomeRepository =
        HomeRepository(api)

    @Provides
    @Singleton
    fun provideGrammarRepository(api: ApiService): GrammarRepository =
        GrammarRepository(api)

    @Provides
    @Singleton
    fun provideNotificationRepository(api: ApiService): NotificationRepository =
        NotificationRepository(api)
}
