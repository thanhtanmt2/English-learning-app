package com.example.english_learning_app.ui.grammar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import androidx.lifecycle.viewmodel.compose.viewModel

// Giao diện thêm mới Bài học Ngữ pháp
@Composable
fun AddEditGrammarScreen(
    navController: NavHostController,
    viewModel: GrammarViewModel = viewModel(), // Tự động lấy ViewModel mà không làm lỗi MainActivity
    noteId: String? = null
) {
    // Các biến trạng thái lưu tạm chữ đang gõ (Local State)
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var commonMistakes by remember { mutableStateOf("") }

    // Kéo dữ liệu cũ điền vào nếu là chế độ Sửa
    LaunchedEffect(noteId) {
        if (noteId != null) {
            val note = viewModel.getGrammarNoteById(noteId)
            if (note != null) {
                title = note.title
                category = note.category
                formula = note.formula
                explanation = note.explanation
                example = note.example
                commonMistakes = note.commonMistakes
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nút quay lại
        TextButton(onClick = { navController.popBackStack() }) {
            Text("⬅ Quay lại")
        }

        Text(text = if (noteId != null) "CHỈNH SỬA BÀI HỌC" else "THÊM BÀI HỌC MỚI", fontSize = 24.sp, modifier = Modifier.padding(vertical = 16.dp))

        // Ô nhập Tiêu đề
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Tên bài học (VD: Hiện tại đơn)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Ô nhập Thể loại
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Thể loại (VD: Thì động từ)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Ô nhập Công thức
        OutlinedTextField(
            value = formula,
            onValueChange = { formula = it },
            label = { Text("Công thức (VD: S + V + O)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Ô nhập Giải thích
        OutlinedTextField(
            value = explanation,
            onValueChange = { explanation = it },
            label = { Text("Giải thích chi tiết") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3 // Cho phép ô nhập to ra để gõ văn bản dài
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Ô nhập Ví dụ
        OutlinedTextField(
            value = example,
            onValueChange = { example = it },
            label = { Text("Ví dụ minh họa") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Ô nhập Lỗi sai hay gặp
        OutlinedTextField(
            value = commonMistakes,
            onValueChange = { commonMistakes = it },
            label = { Text("Lỗi sai hay gặp (Tùy chọn)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị báo lỗi / trạng thái xử lý
        if (viewModel.isLoading.value) {
            Text("Đang lưu...", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        } else if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(text = viewModel.errorMessage.value, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tự động lùi trang khi lưu thành công
        LaunchedEffect(viewModel.isAddSuccess.value) {
            if (viewModel.isAddSuccess.value) {
                navController.popBackStack()
            }
        }

        // Nút Lưu / Cập nhật
        Button(
            onClick = { 
                if (noteId != null) {
                    viewModel.updateGrammarNote(noteId, title, category, formula, explanation, example, commonMistakes)
                } else {
                    viewModel.addGrammarNote(title, category, formula, explanation, example, commonMistakes)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && !viewModel.isLoading.value // Bắt buộc phải có Tiêu đề mới cho Lưu
        ) {
            Text(if (noteId != null) "Cập nhật bài học" else "Lưu bài học")
        }
    }
}
