package com.example.english_learning_app.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            val records = viewModel.progressRecords.value
            
            // Tính toán Streak (Chuỗi ngày) và Accuracy (% đúng trắc nghiệm)
            val totalQuiz = records.sumOf { it.quizScore }
            val accuracy = if (records.isNotEmpty()) (totalQuiz * 100) / (records.size * 5) else 0 // Giả sử mỗi bài có 5 câu
            val streak = records.size // Đơn giản hóa: Số record liên tiếp

            // --- Phần Dashboard ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DashboardCard("🔥 Streak", "$streak Ngày", Color(0xFFFF9800))
                DashboardCard("🎯 Accuracy", "$accuracy%", Color(0xFF4CAF50))
            }

            Text("Thời gian học (Phút)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // --- Phần Biểu đồ ---
            StudyTimeBarChart(records = records)
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Chi tiết các ngày", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

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

// Hàm phụ trợ vẽ Ô hiển thị thông tin (Dashboard)
@Composable
fun DashboardCard(title: String, value: String, color: Color) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

// Hàm phụ trợ tự vẽ Biểu đồ cột (Bar Chart) bằng Canvas
@Composable
fun StudyTimeBarChart(records: List<com.example.english_learning_app.data.model.ProgressRecord>) {
    if (records.isEmpty()) return

    // Lấy tối đa 7 ngày gần nhất
    val chartData = records.takeLast(7).map { it.studyTimeMinutes.toFloat() }
    val maxTime = chartData.maxOrNull() ?: 1f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(16.dp)
    ) {
        val barWidth = size.width / (chartData.size * 2)
        
        chartData.forEachIndexed { index, time ->
            val barHeight = (time / maxTime) * size.height
            val startX = index * (barWidth * 2) + barWidth / 2
            val startY = size.height - barHeight

            drawRoundRect(
                color = Color(0xFF2196F3), // Màu xanh dương bựt
                topLeft = Offset(startX, startY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    }
}
