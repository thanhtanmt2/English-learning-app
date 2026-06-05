package com.example.english_learning_app.ui.learning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.R
import com.example.english_learning_app.ui.utils.rememberTtsSpeaker

@Composable
fun DictationScreen(
    navController: NavHostController,
    wordSetId: String? = null,
    onWordSetChanged: (Int) -> Unit = {},
    viewModel: DictationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
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
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = uiState.wordSet?.name ?: stringResource(R.string.learning_choose_set),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6C757D)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Text(text = stringResource(R.string.learning_loading), color = Color(0xFF6C757D))
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

        // Danh sách chọn bộ từ
        if (uiState.wordSet == null) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.wordSets) { set ->
                    Card(
                        onClick = {
                            viewModel.selectWordSet(set)
                            onWordSetChanged(set.id)
                        },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F2)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = set.name, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = set.description ?: "",
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
        if (word == null && !uiState.isLoading) {
            Text(text = stringResource(R.string.learning_no_words), color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.clearSelection() }) {
                Text(stringResource(R.string.learning_choose_another_set))
            }
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { viewModel.previousWord() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = stringResource(R.string.flashcard_cd_prev))
            }
            IconButton(onClick = { speak(word?.word ?: "") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = stringResource(R.string.flashcard_cd_listen))
            }
            IconButton(onClick = { viewModel.nextWord() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = stringResource(R.string.flashcard_cd_next))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "${stringResource(R.string.learning_meaning_prefix)}${word?.meaning ?: ""}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF4A4E69)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.answer,
            onValueChange = { viewModel.updateAnswer(it) },
            label = { Text(stringResource(R.string.learning_enter_english)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { viewModel.checkAnswer() },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.answer.isNotBlank()
        ) {
            Text(text = stringResource(R.string.learning_check))
        }

        if (uiState.feedback != null) {
            val isCorrect = uiState.feedback == DictationFeedback.CORRECT
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isCorrect) "🎉 ${stringResource(R.string.learning_correct)}" else stringResource(R.string.learning_wrong),
                color = if (isCorrect) Color(0xFF2D6A4F) else Color(0xFFB00020),
                fontWeight = FontWeight.Bold
            )
            if (uiState.sessionTotal > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(R.string.learning_session_score)}${uiState.sessionScore}/${uiState.sessionTotal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6C757D)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = { viewModel.clearSelection() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.learning_change_set), color = Color.Gray)
        }
    }
}
