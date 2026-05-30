package com.example.english_learning_app.ui.grammar

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.GrammarNote
import com.example.english_learning_app.data.model.QuizQuestion
import com.example.english_learning_app.data.remote.RetrofitClient
import kotlinx.coroutines.launch

// "Bộ não" quản lý dữ liệu Ngữ pháp và Trắc nghiệm
class GrammarViewModel : ViewModel() {

    // Danh sách bài học và câu hỏi được lưu tại đây để giao diện lấy ra dùng
    var grammarNotes = mutableStateOf<List<GrammarNote>>(emptyList())
    var quizQuestions = mutableStateOf<List<QuizQuestion>>(emptyList())

    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    // Hàm gọi mạng để tải danh sách Bài học
    fun fetchGrammarNotes() {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                // Kéo dữ liệu từ API về và gán vào biến grammarNotes
                val notes = RetrofitClient.apiService.getGrammarNotes()
                grammarNotes.value = notes
            } catch (e: Exception) {
                errorMessage.value = "Lỗi tải ngữ pháp: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Hàm gọi mạng để tải danh sách Trắc nghiệm
    fun fetchQuizQuestions() {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                val quizzes = RetrofitClient.apiService.getGrammarQuizzes()
                quizQuestions.value = quizzes
            } catch (e: Exception) {
                errorMessage.value = "Lỗi tải trắc nghiệm: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
