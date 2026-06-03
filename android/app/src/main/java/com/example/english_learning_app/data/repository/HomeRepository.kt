package com.example.english_learning_app.data.repository

import com.example.english_learning_app.data.model.ProgressOverview
import com.example.english_learning_app.data.model.User
import com.example.english_learning_app.data.model.WordSet
import com.example.english_learning_app.data.remote.ApiService

class HomeRepository(private val apiService: ApiService) {
    suspend fun loadHomeData(): HomeData {
        // Lưu ý: Real Backend dùng token để lấy user hiện tại, 
        // ở đây ta lấy danh sách wordsets chung hoặc theo user_id nếu có
        val wordSets = apiService.getWordSets()
        val progress = try {
            apiService.getProgressOverview()
        } catch (e: Exception) {
            null
        }
        
        // Tạo User giả từ dữ liệu có sẵn vì API getWordSets không trả về user
        val dummyUser = User(id = "1", name = "Người học", email = "", goal = "", level = "", avatarUrl = null, createdAt = null)

        return HomeData(user = dummyUser, wordSets = wordSets, progress = progress)
    }
}

data class HomeData(
    val user: User,
    val wordSets: List<WordSet>,
    val progress: ProgressOverview?
)
