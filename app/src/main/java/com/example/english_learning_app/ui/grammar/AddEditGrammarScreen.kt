package com.example.english_learning_app.ui.grammar

import androidx.activity.compose.BackHandler
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
    viewModel: GrammarViewModel = viewModel(),
    noteId: String? = null
) {
    // Các biến trạng thái lưu tạm chữ đang gõ (Local State)
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var commonMistakes by remember { mutableStateOf("") }

    // Trạng thái hiện Popup xác nhận thoát
    var showExitDialog by remember { mutableStateOf(false) }

    // Biến để theo dõi xem dữ liệu ban đầu đã được load xong chưa (để tránh hiện popup ngay khi vừa mở)
    var isDataLoaded by remember { mutableStateOf(false) }

    // Kiểm tra xem người dùng có đang nhập dở hay không
    val hasChanges = if (noteId != null) {
        // Trong chế độ sửa: Chỉ báo có thay đổi nếu dữ liệu khác với lúc mới load xong
        val original = viewModel.getGrammarNoteById(noteId)
        isDataLoaded && original != null && (
            title != original.title ||
            category != original.category ||
            formula != original.formula ||
            explanation != original.explanation ||
            example != original.example ||
            commonMistakes != original.commonMistakes
        )
    } else {
        // Trong chế độ thêm: Chỉ cần một ô không trống là tính là có thay đổi
        title.isNotBlank() || category.isNotBlank() || formula.isNotBlank() || 
        explanation.isNotBlank() || example.isNotBlank() || commonMistakes.isNotBlank()
    }

    // Xử lý nút Back của hệ thống
    BackHandler(enabled = hasChanges) {
        showExitDialog = true
    }

    // Kéo dữ liệu cũ điền vào nếu là chế độ Sửa
    LaunchedEffect(noteId) {
        if (noteId != null) {
            // Nếu danh sách trống (ví dụ vào thẳng link edit), fetch lại
            if (viewModel.grammarNotes.value.isEmpty()) {
                viewModel.fetchGrammarNotes()
            }
            val note = viewModel.getGrammarNoteById(noteId)
            if (note != null) {
                title = note.title
                category = note.category
                formula = note.formula
                explanation = note.explanation
                example = note.example
                commonMistakes = note.commonMistakes
                isDataLoaded = true // Đã điền xong dữ liệu cũ
            }
        }
    }

    // Giao diện Popup xác nhận thoát
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Xác nhận thoát") },
            text = { Text("Bạn đang nhập liệu dở, bạn có chắc chắn muốn thoát mà không lưu không?") },
            confirmButton = {
                TextButton(onClick = { 
                    showExitDialog = false
                    navController.popBackStack() 
                }) {
                    Text("Thoát", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Ở lại")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nút quay lại (Cũng cần check popup)
        TextButton(onClick = { 
            if (hasChanges) showExitDialog = true else navController.popBackStack() 
        }) {
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
