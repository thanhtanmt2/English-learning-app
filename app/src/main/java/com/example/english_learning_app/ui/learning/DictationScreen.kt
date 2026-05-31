package com.example.english_learning_app.ui.learning

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.ui.common.rememberTtsSpeaker

@Composable
fun DictationScreen(
    navController: NavHostController,
    wordSetId: String? = null,
    viewModel: LearningViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var answer by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    val speak = rememberTtsSpeaker()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    LaunchedEffect(wordSetId, uiState.wordSets) {
        if (!wordSetId.isNullOrBlank() && uiState.wordSet == null && uiState.wordSets.isNotEmpty()) {
            viewModel.selectWordSetById(wordSetId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            IconButton(onClick = {
                navController.navigate("main") {
                    popUpTo("main") { inclusive = false }
                    launchSingleTop = true
                }
            }) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
            }
            IconButton(onClick = { viewModel.clearSelection() }) {
                Icon(imageVector = Icons.Filled.SwapHoriz, contentDescription = "Word set")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = uiState.wordSet?.name ?: "Chon bo tu de bat dau",
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

        if (uiState.wordSet == null) {
            LazyColumn(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
                items(uiState.wordSets) { set ->
                    Card(
                        onClick = { viewModel.selectWordSet(set) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F2)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = set.name, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = set.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6C757D)
                            )
                        }
                    }
                }
            }
            return@Column
        }

        val word = uiState.words.getOrNull(uiState.currentIndex)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {
                viewModel.previousWord()
                answer = ""
                feedback = null
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Previous")
            }
            IconButton(onClick = { speak(word?.word ?: "") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen")
            }
            IconButton(onClick = {
                viewModel.nextWord()
                answer = ""
                feedback = null
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Next")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
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
    }
}
