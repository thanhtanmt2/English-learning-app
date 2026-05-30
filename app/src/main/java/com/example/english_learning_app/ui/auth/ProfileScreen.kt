package com.example.english_learning_app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val user = viewModel.currentUser.value
    
    // Nếu chưa đăng nhập (không có user), quay lại
    if (user == null) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    var name by remember { mutableStateOf(user.name) }
    var expandedGoal by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf(user.goal ?: "IELTS") }
    val goals = listOf("IELTS", "TOEIC", "Giao tiếp")

    var expandedLevel by remember { mutableStateOf(false) }
    var selectedLevel by remember { mutableStateOf(user.level ?: "A1") }
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onNavigateBack) { Text("⬅ Quay lại") }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "TRANG CÁ NHÂN", fontSize = 20.sp)
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp)) // Để cân bằng nút back
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Họ và Tên") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chọn Goal
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
            DropdownMenu(expanded = expandedGoal, onDismissRequest = { expandedGoal = false }) {
                goals.forEach { goal ->
                    DropdownMenuItem(text = { Text(goal) }, onClick = {
                        selectedGoal = goal
                        expandedGoal = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chọn Level
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
            DropdownMenu(expanded = expandedLevel, onDismissRequest = { expandedLevel = false }) {
                levels.forEach { level ->
                    DropdownMenuItem(text = { Text(level) }, onClick = {
                        selectedLevel = level
                        expandedLevel = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(text = viewModel.errorMessage.value, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { viewModel.updateProfile(name, selectedGoal, selectedLevel) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading.value
        ) {
            Text(if (viewModel.isLoading.value) "Đang lưu..." else "Lưu thay đổi")
        }
    }
}
