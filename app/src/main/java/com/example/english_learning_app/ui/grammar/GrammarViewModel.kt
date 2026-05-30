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
    var isAddSuccess = mutableStateOf(false) // Báo hiệu đã thêm xong

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

    // Hàm gửi bài học mới lên mạng
    fun addGrammarNote(title: String, category: String, formula: String) {
        isLoading.value = true
        errorMessage.value = ""
        isAddSuccess.value = false
        viewModelScope.launch {
            try {
                // Tạo khuôn chứa thông tin mới (ID tự tạo theo thời gian)
                val newNote = GrammarNote(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    category = category,
                    formula = formula,
                    explanation = "",
                    example = "",
                    commonMistakes = "",
                    tags = emptyList(),
                    easeFactor = 2.5,
                    interval = 0,
                    nextReviewDate = ""
                )
                // Nhờ Retrofit gửi lên server
                RetrofitClient.apiService.addGrammarNote(newNote)
                
                // Báo thành công và tải lại danh sách mới
                isAddSuccess.value = true
                fetchGrammarNotes()
            } catch (e: Exception) {
                errorMessage.value = "Lỗi thêm bài học: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
