package com.example.english_learning_app.ui.vocabulary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.R

@Composable
fun WordSetListScreen(
    navController: NavHostController,
    refresh: Boolean = false,
    viewModel: WordSetListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }
    var fabExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(refresh) {
        viewModel.load(force = refresh)
    }

    if (fabExpanded) {
        BackHandler { fabExpanded = false }
    }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(visible = fabExpanded) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SpeedDialItem(
                            icon = Icons.Default.Quiz,
                            label = "Quiz",
                            containerColor = Color(0xFF4A4E69),
                            onClick = {
                                fabExpanded = false
                                navController.navigate("word_quiz_setup")
                            }
                        )
                        SpeedDialItem(
                            icon = Icons.Default.Add,
                            label = "Thêm chủ đề",
                            containerColor = Color(0xFF457B9D),
                            onClick = {
                                fabExpanded = false
                                navController.navigate("add_word_set")
                            }
                        )
                        SpeedDialItem(
                            icon = Icons.Default.AutoAwesome,
                            label = "Tạo bằng AI",
                            containerColor = Color(0xFF6C63FF),
                            onClick = {
                                fabExpanded = false
                                navController.navigate("ai_word_list")
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = if (fabExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.word_set_list_title),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.word_list_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A4E69)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Text(text = stringResource(R.string.common_loading), color = Color(0xFF6C757D))
                return@Column
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = "${stringResource(R.string.learning_load_error)}${uiState.errorMessage}",
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
                                text = wordSet.description ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6C757D)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.word_set_list_progress, wordSet.learnedWords, wordSet.totalWords),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF457B9D)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                TextButton(onClick = { navController.navigate("edit_word_set/${wordSet.id}") }) {
                                    Text(text = stringResource(R.string.word_set_list_edit))
                                }
                                TextButton(onClick = { pendingDeleteId = wordSet.id.toString() }) {
                                    Text(text = stringResource(R.string.word_set_list_delete), color = Color(0xFFB00020))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text(text = stringResource(R.string.word_set_list_delete_title)) },
            text = { Text(text = stringResource(R.string.word_set_list_delete_confirm)) },
            confirmButton = {
                Button(onClick = {
                    val wordSetId = pendingDeleteId
                    pendingDeleteId = null
                    if (wordSetId != null) viewModel.deleteWordSet(wordSetId)
                }) {
                    Text(text = stringResource(R.string.word_set_list_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    Text(text = stringResource(R.string.grammar_add_edit_stay))
                }
            }
        )
    }
}

@Composable
private fun SpeedDialItem(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF1C1B1F).copy(alpha = 0.75f)
        ) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = containerColor
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
