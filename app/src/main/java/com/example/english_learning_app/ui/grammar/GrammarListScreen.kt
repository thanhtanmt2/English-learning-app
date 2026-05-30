package com.example.english_learning_app.ui.grammar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Giao diện màn hình Danh sách Bài học Ngữ pháp
@Composable
fun GrammarListScreen(
    viewModel: GrammarViewModel,
    onNavigateToAdd: () -> Unit = {},
    onNavigateToQuiz: () -> Unit = {}
) {
    // Ngay khi mở màn hình, tự động gọi API lấy danh sách bài học
    LaunchedEffect(Unit) {
        viewModel.fetchGrammarNotes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "BÀI HỌC NGỮ PHÁP", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Nút đi tới màn hình làm bài Trắc nghiệm
        Button(
            onClick = onNavigateToQuiz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Làm bài Trắc nghiệm")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị chữ báo lỗi (nếu có)
        if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(text = viewModel.errorMessage.value, color = MaterialTheme.colorScheme.error)
        }

        // Hiện chữ Loading hoặc Danh sách
        if (viewModel.isLoading.value) {
            Text("Đang tải dữ liệu...")
        } else {
            // LazyColumn dùng để vẽ danh sách cuộn được (giống RecyclerView)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.grammarNotes.value) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { /* Tương lai sẽ bấm vào để xem chi tiết hoặc sửa */ },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = note.title, fontSize = 20.sp)
                            Text(text = note.category, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút Thêm bài học mới
        Button(
            onClick = onNavigateToAdd,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thêm bài học mới")
        }
    }
}
