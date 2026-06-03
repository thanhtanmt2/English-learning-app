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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_learning_app.data.model.GrammarNote

@Composable
fun GrammarListScreen(
    viewModel: GrammarViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {},
    onNavigateToEdit: (String) -> Unit = {},
    onNavigateToDetail: (GrammarNote) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchGrammarNotes()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "BÀI HỌC NGỮ PHÁP", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.errorMessage.isNotEmpty()) {
            Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
        }

        if (uiState.isLoading) {
            Text("Đang tải dữ liệu...")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.grammarNotes) { note ->
                    val isCompleted = note.highestScore != null
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onNavigateToDetail(note) },
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCompleted) androidx.compose.ui.graphics.Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = note.title,
                                fontSize = 20.sp,
                                color = if (isCompleted) androidx.compose.ui.graphics.Color(0xFF2E7D32) else androidx.compose.ui.graphics.Color.Unspecified
                            )
                            Text(text = note.category, color = MaterialTheme.colorScheme.secondary)

                            if (isCompleted) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Điểm cao nhất: ${note.highestScore}/${note.totalQuestions}",
                                    color = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                IconButton(onClick = { onNavigateToEdit(note.id) }) { Text("✏️") }
                                IconButton(onClick = { viewModel.deleteGrammarNote(note.id) }) { Text("🗑️") }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToAdd, modifier = Modifier.fillMaxWidth()) {
            Text("Thêm bài học mới")
        }
    }
}
