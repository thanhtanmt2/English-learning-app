package com.example.english_learning_app.ui.learning

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.R
import com.example.english_learning_app.ui.utils.rememberTtsSpeaker

@Composable
fun FlashcardScreen(
    navController: NavHostController,
    wordSetId: String? = null,
    onWordSetChanged: (Int) -> Unit = {},
    viewModel: FlashcardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isFlipped by remember { mutableStateOf(false) }
    val speak = rememberTtsSpeaker()
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 320)
    )

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

        // Flashcard
        val word = uiState.words.getOrNull(uiState.currentIndex)
        Card(
            onClick = { isFlipped = !isFlipped },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F2)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12 * density
                }
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                if (rotation <= 90f) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(bottom = 48.dp)
                            .alpha(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = word?.word ?: "",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.learning_choose_set),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6C757D)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(bottom = 48.dp)
                            .graphicsLayer { rotationY = 180f }
                            .alpha(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = word?.meaning ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4A4E69),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (!word?.example.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "${stringResource(R.string.word_list_example_prefix)}${word!!.example}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6C757D),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Khi chưa lật: nút Prev + Phát âm + Next (không submit)
                // Khi đã lật: 3 nút đánh giá SM-2
                if (!isFlipped) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.previousWord()
                            isFlipped = false
                        }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = stringResource(R.string.flashcard_cd_prev))
                        }
                        IconButton(onClick = { speak(word?.word ?: "") }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = stringResource(R.string.flashcard_cd_listen))
                        }
                        IconButton(onClick = {
                            viewModel.nextWord()
                        }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = stringResource(R.string.flashcard_cd_next))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút đánh giá SM-2 — chỉ hiện sau khi lật thẻ
        if (isFlipped) {
            Text(
                text = stringResource(R.string.flashcard_recall_question),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Khó — quality 1: backend reset interval về 1
                OutlinedButton(
                    onClick = {
                        viewModel.submitReview(1)
                        viewModel.nextWord()
                        isFlipped = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.flashcard_difficulty_hard), fontSize = 13.sp)
                }
                // Được — quality 3: interval tăng bình thường
                Button(
                    onClick = {
                        viewModel.submitReview(3)
                        viewModel.nextWord()
                        isFlipped = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(stringResource(R.string.flashcard_difficulty_ok), fontSize = 13.sp)
                }
                // Dễ — quality 5: interval tăng nhanh
                Button(
                    onClick = {
                        viewModel.submitReview(5)
                        viewModel.nextWord()
                        isFlipped = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text(stringResource(R.string.flashcard_difficulty_easy), fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.clearSelection() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.learning_change_set))
        }
    }
}
