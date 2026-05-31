package com.example.english_learning_app.ui.learning

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.ui.common.rememberTtsSpeaker

@Composable
fun FlashcardScreen(
    navController: NavHostController,
    wordSetId: String? = null,
    viewModel: LearningViewModel = viewModel()
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
        Text(text = "Flashcard", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
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
                    Column(modifier = Modifier.alpha(1f)) {
                        Text(
                            text = word?.word ?: "No words",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap to flip",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6C757D)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .graphicsLayer { rotationY = 180f }
                            .alpha(1f)
                    ) {
                        Text(
                            text = word?.meaning ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF4A4E69)
                        )
                        if (!word?.example.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
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

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { speak(word?.word ?: "") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Listen")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { viewModel.clearSelection() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Change word set")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                // Nếu đã lật thẻ xem nghĩa thì tính là đã học (quality 4)
                if (isFlipped) {
                    viewModel.submitReview(4)
                }
                viewModel.nextWord()
                isFlipped = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Next Word")
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
