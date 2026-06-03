package com.example.english_learning_app.ui.grammar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun AddEditGrammarScreen(
    navController: NavHostController,
    viewModel: GrammarViewModel,
    noteId: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var commonMistakes by remember { mutableStateOf("") }
    var showExitDialog by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }

    val hasChanges = if (noteId != null) {
        val original = viewModel.getGrammarNoteById(noteId)
        isDataLoaded && original != null && (
            title != original.title || category != original.category ||
            formula != original.formula || explanation != original.explanation ||
            example != original.example || commonMistakes != original.commonMistakes
        )
    } else {
        title.isNotBlank() || category.isNotBlank() || formula.isNotBlank() ||
        explanation.isNotBlank() || example.isNotBlank() || commonMistakes.isNotBlank()
    }

    BackHandler(enabled = hasChanges) { showExitDialog = true }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            if (uiState.grammarNotes.isEmpty()) {
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
                isDataLoaded = true
            }
        }
    }

    LaunchedEffect(uiState.isAddSuccess) {
        if (uiState.isAddSuccess) {
            viewModel.resetAddSuccess()
            navController.popBackStack()
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Xác nhận thoát") },
            text = { Text("Bạn đang nhập liệu dở, bạn có chắc chắn muốn thoát mà không lưu không?") },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; navController.popBackStack() }) {
                    Text("Thoát", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Ở lại") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = { if (hasChanges) showExitDialog = true else navController.popBackStack() }) {
            Text("⬅ Quay lại")
        }

        Text(
            text = if (noteId != null) "CHỈNH SỬA BÀI HỌC" else "THÊM BÀI HỌC MỚI",
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tên bài học (VD: Hiện tại đơn)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Thể loại (VD: Thì động từ)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = formula, onValueChange = { formula = it }, label = { Text("Công thức (VD: S + V + O)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = explanation, onValueChange = { explanation = it }, label = { Text("Giải thích chi tiết") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = example, onValueChange = { example = it }, label = { Text("Ví dụ minh họa") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = commonMistakes, onValueChange = { commonMistakes = it }, label = { Text("Lỗi sai hay gặp (Tùy chọn)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Text("Đang lưu...", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        } else if (uiState.errorMessage.isNotEmpty()) {
            Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (noteId != null) {
                    viewModel.updateGrammarNote(noteId, title, category, formula, explanation, example, commonMistakes)
                } else {
                    viewModel.addGrammarNote(title, category, formula, explanation, example, commonMistakes)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && !uiState.isLoading
        ) {
            Text(if (noteId != null) "Cập nhật bài học" else "Lưu bài học")
        }
    }
}
