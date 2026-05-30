package com.example.english_learning_app.ui.progress

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.ProgressOverview
import com.example.english_learning_app.data.model.ProgressRecord
import com.example.english_learning_app.data.remote.RetrofitClient
import kotlinx.coroutines.launch

// "Bộ não" quản lý dữ liệu cho màn hình Tiến độ
class ProgressViewModel : ViewModel() {

    // Dữ liệu tổng quan (Streak, Accuracy...)
    var progressOverview = mutableStateOf<ProgressOverview?>(null)
    // Danh sách lịch sử học tập hàng ngày
    var progressRecords = mutableStateOf<List<ProgressRecord>>(emptyList())
    
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    // Hàm gọi mạng để tải toàn bộ Tiến độ
    fun fetchProgress() {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                // Tải tổng quan
                val overviews = RetrofitClient.apiService.getProgressOverview()
                if (overviews.isNotEmpty()) {
                    progressOverview.value = overviews.first() // Lấy cái của User hiện tại (giả định)
                }

                // Tải lịch sử chi tiết
                val history = RetrofitClient.apiService.getProgress()
                progressRecords.value = history.reversed() // Hiện ngày mới nhất lên đầu
            } catch (e: Exception) {
                errorMessage.value = "Lỗi tải tiến độ: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
