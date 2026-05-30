package com.example.english_learning_app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Giao diện của màn hình Đăng ký
@Composable
fun RegisterScreen(viewModel: AuthViewModel, onNavigateToLogin: () -> Unit = {}) {
    
    // Tự động chuyển hướng về trang đăng nhập khi đăng ký thành công
    LaunchedEffect(viewModel.isRegisterSuccess.value) {
        if (viewModel.isRegisterSuccess.value) {
            onNavigateToLogin()
            viewModel.isRegisterSuccess.value = false // Reset trạng thái
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "ĐĂNG KÝ TÀI KHOẢN", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))

        // 1. Họ và Tên
        OutlinedTextField(
            value = viewModel.name.value,
            onValueChange = { viewModel.name.value = it },
            label = { Text("Họ và Tên") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 2. Email
        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 3. Mật khẩu
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 4. Mục tiêu học tập (Goal)
        var expandedGoal by remember { mutableStateOf(false) }
        var selectedGoal by remember { mutableStateOf("IELTS") }
        val goals = listOf("IELTS", "TOEIC", "Giao tiếp")

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedGoal,
                onValueChange = {},
                readOnly = true,
                label = { Text("Mục tiêu học tập") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expandedGoal = !expandedGoal }) {
                        Text("▼")
                    }
                }
            )
            DropdownMenu(
                expanded = expandedGoal,
                onDismissRequest = { expandedGoal = false }
            ) {
                goals.forEach { goal ->
                    DropdownMenuItem(
                        text = { Text(goal) },
                        onClick = {
                            selectedGoal = goal
                            expandedGoal = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 5. Trình độ hiện tại (Level)
        var expandedLevel by remember { mutableStateOf(false) }
        var selectedLevel by remember { mutableStateOf("A1") }
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedLevel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Trình độ hiện tại") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expandedLevel = !expandedLevel }) {
                        Text("▼")
                    }
                }
            )
            DropdownMenu(
                expanded = expandedLevel,
                onDismissRequest = { expandedLevel = false }
            ) {
                levels.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            selectedLevel = level
                            expandedLevel = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị báo lỗi / thành công
        if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(text = viewModel.errorMessage.value, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Nút Đăng ký
        Button(
            onClick = { viewModel.register(selectedGoal, selectedLevel) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading.value
        ) {
            if (viewModel.isLoading.value) {
                Text("Đang xử lý...")
            } else {
                Text("Đăng ký")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút quay lại màn hình Đăng nhập
        TextButton(onClick = onNavigateToLogin) {
            Text("Đã có tài khoản? Đăng nhập")
        }
    }
}
