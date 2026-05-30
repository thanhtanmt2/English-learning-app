package com.example.english_learning_app.data.repository

import com.example.english_learning_app.data.model.Progress
import com.example.english_learning_app.data.model.User
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.remote.MockApiService

class HomeRepository(private val apiService: MockApiService) {
    suspend fun loadHomeData(): HomeData {
        val users = apiService.login()
        val user = users.firstOrNull()
            ?: throw IllegalStateException("No users returned from mock API")

        val wordSets = apiService.getWordSets(userId = user.id)
        val progress = apiService.getProgress(userId = user.id).firstOrNull()

        return HomeData(user = user, wordSets = wordSets, progress = progress)
    }
}

data class HomeData(
    val user: User,
    val wordSets: List<WordSet>,
    val progress: Progress?
)
