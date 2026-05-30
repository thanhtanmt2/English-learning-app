package com.example.english_learning_app.ui.grammar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Giao diện thêm mới Bài học Ngữ pháp
@Composable
fun AddEditGrammarScreen(navController: NavHostController) {
    // Các biến trạng thái lưu tạm chữ đang gõ (Local State)
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nút quay lại
        TextButton(onClick = { navController.popBackStack() }) {
            Text("⬅ Quay lại")
        }

        Text(text = "THÊM BÀI HỌC MỚI", fontSize = 24.sp, modifier = Modifier.padding(vertical = 16.dp))

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
        Spacer(modifier = Modifier.height(16.dp))

        // Nút Lưu
        Button(
            onClick = { 
                // Tương lai sẽ gọi ViewModel để gửi dữ liệu lên mạng ở đây 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu bài học")
        }
    }
}
