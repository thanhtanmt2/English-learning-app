package com.example.english_learning_app.ui.learning

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun DictationScreen(
    navController: NavHostController,
    viewModel: LearningViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var answer by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Dictation", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = uiState.wordSet?.name ?: "Dang tai bo tu...",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6C757D)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Text(text = "Dang tai du lieu...", color = Color(0xFF6C757D))
            return@Column
        }

        if (uiState.errorMessage != null) {
            Text(
                text = "Khong the tai du lieu: ${uiState.errorMessage}",
                color = Color(0xFFB00020),
                style = MaterialTheme.typography.bodySmall
            )
            return@Column
        }

        val word = uiState.words.getOrNull(uiState.currentIndex)
        Text(
            text = "Meaning: ${word?.meaning ?: ""}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF4A4E69)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = answer,
            onValueChange = {
                answer = it
                feedback = null
            },
            label = { Text("Type the word") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                val target = word?.word?.trim()?.lowercase() ?: ""
                val typed = answer.trim().lowercase()
                feedback = if (typed.isNotEmpty() && typed == target) {
                    "Correct!"
                } else {
                    "Try again"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Check")
        }
        if (feedback != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = feedback ?: "", color = Color(0xFF2B2D42))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                viewModel.nextWord()
                answer = ""
                feedback = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Next")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Home")
        }
    }
}
