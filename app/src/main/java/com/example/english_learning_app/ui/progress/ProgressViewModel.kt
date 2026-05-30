package com.example.english_learning_app.ui.progress

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.english_learning_app.data.model.ProgressRecord
import com.example.english_learning_app.data.remote.RetrofitClient
import kotlinx.coroutines.launch

// "Bộ não" quản lý dữ liệu cho màn hình Tiến độ
class ProgressViewModel : ViewModel() {

    // Danh sách lịch sử học tập được lấy từ mạng về
    var progressRecords = mutableStateOf<List<ProgressRecord>>(emptyList())
    
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    // Hàm gọi mạng để tải lịch sử Tiến độ
    fun fetchProgress() {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                // Nhờ RetrofitClient gọi mạng lấy dữ liệu
                val records = RetrofitClient.apiService.getProgress()
                
                // Lưu vào biến để Giao diện lấy ra dùng
                progressRecords.value = records
            } catch (e: Exception) {
                errorMessage.value = "Lỗi tải tiến độ: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
