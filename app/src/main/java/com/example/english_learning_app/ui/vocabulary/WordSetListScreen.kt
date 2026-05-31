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
import androidx.compose.material3.AlertDialog
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
fun WordSetListScreen(
    navController: NavHostController,
    refresh: Boolean = false,
    viewModel: WordSetListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(refresh) {
        viewModel.load(force = refresh)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Word Sets",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Chon bo tu de bat dau hoc.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A4E69)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController.navigate("word_quiz_setup") }) {
                    Text(text = "Quiz")
                }
                Button(onClick = { navController.navigate("add_word_set") }) {
                    Text(text = "Add")
                }
            }
        }
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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(uiState.wordSets) { wordSet ->
                Card(
                    onClick = { navController.navigate("word_list/${wordSet.id}") },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F2)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = wordSet.name, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = wordSet.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6C757D)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${wordSet.learnedWords}/${wordSet.totalWords} words learned",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF457B9D)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TextButton(onClick = { navController.navigate("edit_word_set/${wordSet.id}") }) {
                                Text(text = "Edit")
                            }
                            TextButton(onClick = { pendingDeleteId = wordSet.id }) {
                                Text(text = "Delete", color = Color(0xFFB00020))
                            }
                        }
                    }
                }
            }
        }

        if (pendingDeleteId != null) {
            AlertDialog(
                onDismissRequest = { pendingDeleteId = null },
                title = { Text(text = "Delete word set") },
                text = {
                    Text(text = "This will delete the word set and all words inside it. Continue?")
                },
                confirmButton = {
                    Button(onClick = {
                        val wordSetId = pendingDeleteId
                        pendingDeleteId = null
                        if (wordSetId != null) {
                            viewModel.deleteWordSet(wordSetId)
                        }
                    }) {
                        Text(text = "Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingDeleteId = null }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}
