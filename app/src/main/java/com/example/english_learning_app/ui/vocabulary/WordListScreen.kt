package com.example.english_learning_app.ui.vocabulary

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.ui.common.rememberTtsSpeaker

@Composable
fun WordListScreen(
    navController: NavHostController,
    wordSetId: String,
    viewModel: WordListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val speak = rememberTtsSpeaker()

    LaunchedEffect(wordSetId) {
        viewModel.load(wordSetId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Word List", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Danh sách từ trong bộ từ đã chọn.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6C757D)
                )
            }
            Button(onClick = { navController.navigate("add_edit_word/$wordSetId") }) {
                Text(text = "Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { navController.navigate("flashcard?wordSetId=$wordSetId") },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Flashcard")
            }
            Button(
                onClick = { navController.navigate("dictation?wordSetId=$wordSetId") },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Dictation")
            }
        }

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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(uiState.words) { word ->
                Card(
                    onClick = { navController.navigate("add_edit_word/$wordSetId/${word.id}") },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = word.word,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { speak(word.word) }) {
                                Text(text = "Speak")
                            }
                            TextButton(onClick = { viewModel.deleteWord(word.id.toString()) }) {
                                Text(text = "Delete")
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = word.meaning,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4A4E69)
                        )
                        if (!word.example.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Example: ${word.example}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6C757D)
                            )
                        }
                    }
                }
            }
        }
    }
}

