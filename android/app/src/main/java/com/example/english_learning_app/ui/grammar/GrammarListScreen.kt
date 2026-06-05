package com.example.english_learning_app.ui.grammar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_learning_app.R
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
    val grouped = uiState.grammarNotes.groupBy { it.category }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.grammar_list_add),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.grammar_list_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (uiState.errorMessage.isNotEmpty()) {
                Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }

            if (uiState.isLoading) {
                Text(stringResource(R.string.common_loading))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    grouped.forEach { (category, notes) ->
                        stickyHeader(key = "header_$category") {
                            CategoryHeader(category = category, count = notes.size)
                        }
                        items(notes, key = { it.id }) { note ->
                            GrammarNoteCard(
                                note = note,
                                onNavigateToDetail = onNavigateToDetail,
                                onNavigateToEdit = onNavigateToEdit,
                                onDelete = { viewModel.deleteGrammarNote(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$count bài",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GrammarNoteCard(
    note: GrammarNote,
    onNavigateToDetail: (GrammarNote) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val hasScore = note.highestScore != null && note.totalQuestions != null && note.totalQuestions > 0
    val progress = if (hasScore) note.highestScore!!.toFloat() / note.totalQuestions!! else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onNavigateToDetail(note) },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = note.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Tùy chọn"
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("✏️  Chỉnh sửa") },
                            onClick = {
                                menuExpanded = false
                                onNavigateToEdit(note.id)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🗑️  Xóa") },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            if (hasScore) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🏆 Điểm cao nhất: ${note.highestScore}/${note.totalQuestions}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}
