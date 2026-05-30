package com.example.english_learning_app.ui.grammar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_learning_app.data.model.GrammarNote

// Giao diện màn hình Danh sách Bài học Ngữ pháp
@Composable
fun GrammarListScreen(
    viewModel: GrammarViewModel,
    onNavigateBack: () -> Unit = {}, // Thêm callback quay lại
    onNavigateToAdd: () -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {},
    onNavigateToQuiz: () -> Unit = {},
    onNavigateToDetail: (GrammarNote) -> Unit = {}
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
        // Thêm nút Quay lại ở trên cùng
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "BÀI HỌC NGỮ PHÁP", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

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
                            .clickable { onNavigateToDetail(note) },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = note.title, fontSize = 20.sp)
                            Text(text = note.category, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                IconButton(onClick = { onNavigateToEdit(note.id) }) {
                                    Text("✏️")
                                }
                                IconButton(onClick = { viewModel.deleteGrammarNote(note.id) }) {
                                    Text("🗑️")
                                }
                            }
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
