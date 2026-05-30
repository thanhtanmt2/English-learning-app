package com.example.english_learning_app.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Giao diện hiển thị Tiến độ học tập
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onNavigateBack: () -> Unit = {}
) {
    // Tự động kéo dữ liệu tiến độ về khi mở màn hình
    LaunchedEffect(Unit) {
        viewModel.fetchProgress()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nút quay lại
        TextButton(onClick = onNavigateBack) {
            Text("⬅ Quay lại")
        }

        Text(text = "TIẾN ĐỘ HỌC TẬP", fontSize = 24.sp, modifier = Modifier.padding(vertical = 16.dp))

        if (viewModel.isLoading.value) {
            Text("Đang tải dữ liệu...")
        } else if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(text = viewModel.errorMessage.value, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.progressRecords.value) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Ngày: ${record.date}", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "📚 Từ vựng đã học: ${record.wordsLearned}")
                            Text(text = "📝 Ngữ pháp hoàn thành: ${record.grammarCompleted}")
                            Text(text = "🎯 Điểm trắc nghiệm: ${record.quizScore}")
                            Text(text = "⏱ Thời gian học: ${record.studyTimeMinutes} phút")
                        }
                    }
                }
            }
        }
    }
}
