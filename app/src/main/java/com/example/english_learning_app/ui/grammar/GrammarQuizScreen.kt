package com.example.english_learning_app.ui.grammar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Giao diện màn hình Làm bài Trắc nghiệm Ngữ pháp
@Composable
fun GrammarQuizScreen(
    viewModel: GrammarViewModel,
    onNavigateBack: () -> Unit = {}
) {
    // Tự động kéo danh sách trắc nghiệm về khi mở màn hình
    LaunchedEffect(Unit) {
        viewModel.fetchQuizQuestions()
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

        Text(text = "BÀI TẬP TRẮC NGHIỆM", fontSize = 24.sp, modifier = Modifier.padding(vertical = 16.dp))

        if (viewModel.isLoading.value) {
            Text("Đang tải bài tập...")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.quizQuestions.value) { quiz ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Câu hỏi
                            Text(text = "Hỏi: ${quiz.question}", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // 4 Đáp án
                            quiz.options.forEach { option ->
                                OutlinedButton(
                                    onClick = { /* Tương lai sẽ thêm tính năng chấm điểm đúng/sai */ },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Text(text = option)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
