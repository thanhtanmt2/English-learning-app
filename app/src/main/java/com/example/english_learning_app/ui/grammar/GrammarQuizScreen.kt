package com.example.english_learning_app.ui.grammar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        // Bộ nhớ lưu các câu đã chọn (Câu hỏi ID -> Đáp án đã chọn)
        val selectedAnswers = remember { mutableStateMapOf<String, String>() }
        
        // Tự động tính điểm hiện tại
        val score = viewModel.quizQuestions.value.count { quiz ->
            selectedAnswers[quiz.id] == quiz.correctAnswer
        }
        val total = viewModel.quizQuestions.value.size

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "BÀI TẬP TRẮC NGHIỆM", fontSize = 20.sp)
            Text(text = "Điểm: $score/$total", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        }

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
                                // Logic màu sắc
                                val isAnswered = selectedAnswers.containsKey(quiz.id)
                                val isSelected = selectedAnswers[quiz.id] == option
                                val isCorrect = option == quiz.correctAnswer

                                val buttonColor = if (isAnswered) {
                                    if (isCorrect) Color(0xFF4CAF50) // Xanh lá nếu đúng
                                    else if (isSelected) Color(0xFFF44336) // Đỏ nếu chọn sai
                                    else MaterialTheme.colorScheme.surface
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }

                                val textColor = if (isAnswered && (isCorrect || isSelected)) Color.White else MaterialTheme.colorScheme.primary

                                OutlinedButton(
                                    onClick = { 
                                        // Chỉ cho phép chọn 1 lần
                                        if (!isAnswered) {
                                            selectedAnswers[quiz.id] = option
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = buttonColor,
                                        contentColor = textColor
                                    )
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
