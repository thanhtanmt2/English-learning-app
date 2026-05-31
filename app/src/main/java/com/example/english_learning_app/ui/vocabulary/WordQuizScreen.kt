package com.example.english_learning_app.ui.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.data.model.WordQuizQuestion

@Composable
fun WordQuizScreen(
    navController: NavHostController,
    wordSetIdsParam: String,
    countParam: Int,
    viewModel: WordQuizViewModel = viewModel()
) {
    val wordSetIds = remember(wordSetIdsParam) {
        wordSetIdsParam.split(",").filter { it.isNotBlank() }
    }

    LaunchedEffect(wordSetIdsParam, countParam) {
        viewModel.load(wordSetIds, countParam)
    }

    val uiState by viewModel.uiState.collectAsState()
    val selectedAnswers = remember { mutableStateMapOf<String, String>() }
    var isSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = { navController.popBackStack() },
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("<- Quay lai", fontSize = 16.sp)
        }

        val score = uiState.questions.count { selectedAnswers[it.id] == it.correctAnswer }
        val total = uiState.questions.size

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TRAC NGHIEM TU VUNG",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )

            if (isSubmitted) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Diem: $score/$total",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        if (uiState.actualCount > 0 && uiState.actualCount < uiState.requestedCount) {
            Text(
                text = "Da tao ${uiState.actualCount} cau (toi da tu bo tu duoc chon).",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6C757D)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp, top = 8.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (!uiState.errorMessage.isNullOrBlank()) {
            WordQuizErrorState(message = uiState.errorMessage ?: "Khong the tai du lieu.") {
                isSubmitted = false
                selectedAnswers.clear()
                viewModel.reload()
            }
        } else if (uiState.questions.isEmpty()) {
            WordQuizEmptyState {
                isSubmitted = false
                selectedAnswers.clear()
                viewModel.reload()
            }
        } else {
            val pagerState = rememberPagerState(pageCount = { uiState.questions.size })
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Câu ${pagerState.currentPage + 1} / ${uiState.questions.size}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                pageSpacing = 16.dp
            ) { page ->
                val quiz = uiState.questions[page]
                WordQuizCard(
                    quiz = quiz,
                    selectedOption = selectedAnswers[quiz.id],
                    isSubmitted = isSubmitted,
                    onOptionSelected = { option ->
                        if (!isSubmitted) {
                            selectedAnswers[quiz.id] = option
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                if (!isSubmitted) {
                    val allAnswered = selectedAnswers.size == uiState.questions.size
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Thanh điều hướng câu hỏi
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(count = uiState.questions.size) { index ->
                                val quizId = uiState.questions[index].id
                                val isAnswered = selectedAnswers.containsKey(quizId)
                                val isCurrentPage = pagerState.currentPage == index
                                
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isAnswered) MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .border(
                                            width = if (isCurrentPage) 2.dp else 1.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = if (isAnswered) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { isSubmitted = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = allAnswered,
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("NỘP BÀI", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        if (!allAnswered) {
                            Text(
                                text = "Hoàn thành ${selectedAnswers.size}/${uiState.questions.size} câu để nộp bài",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordQuizCard(
    quiz: WordQuizQuestion,
    selectedOption: String?,
    isSubmitted: Boolean,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hỏi: ${quiz.question}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            quiz.options.forEach { option ->
                val isThisOptionSelected = selectedOption == option
                val isCorrect = option == quiz.correctAnswer

                val containerColor = when {
                    isSubmitted && isCorrect -> Color(0xFFE8F5E9)
                    isSubmitted && isThisOptionSelected -> Color(0xFFFFEBEE)
                    !isSubmitted && isThisOptionSelected -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }

                val contentColor = when {
                    isSubmitted && isCorrect -> Color(0xFF2E7D32)
                    isSubmitted && isThisOptionSelected -> Color(0xFFC62828)
                    !isSubmitted && isThisOptionSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }

                val borderColor = when {
                    isSubmitted && isCorrect -> Color(0xFF4CAF50)
                    isSubmitted && isThisOptionSelected -> Color(0xFFF44336)
                    !isSubmitted && isThisOptionSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                OutlinedButton(
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = containerColor,
                        contentColor = contentColor
                    ),
                    border = BorderStroke(
                        if (isThisOptionSelected || (isSubmitted && isCorrect)) 2.dp else 1.dp,
                        borderColor
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = option, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        if (isSubmitted) {
                            if (isCorrect) Text("OK", fontWeight = FontWeight.Bold)
                            else if (isThisOptionSelected) Text("X", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isSubmitted) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Dap an: ${quiz.correctAnswer}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun WordQuizErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Loi: $message", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text("Thu lai") }
    }
}

@Composable
fun WordQuizEmptyState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Khong co du lieu cau hoi.")
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text("Tai lai") }
    }
}
