package com.example.english_learning_app.ui.grammar

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.english_learning_app.data.model.QuizQuestion

@Composable
fun GrammarQuizScreen(
    viewModel: GrammarViewModel,
    noteId: String,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.fetchQuizQuestions(noteId)
    }

    val questions = viewModel.quizQuestions.value
    val selectedAnswers = remember { mutableStateMapOf<String, String>() }
    var isSubmitted by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = "BÀI TẬP TRẮC NGHIỆM",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            val score = questions.count { selectedAnswers[it.id] == it.correctAnswer }
            val total = questions.size

            if (isSubmitted) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Điểm: $score/$total",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // --- PHẦN NỘI DUNG (CÓ THỂ CUỘN LÊN XUỐNG) ---
            if (viewModel.isLoading.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.errorMessage.value.isNotEmpty()) {
                ErrorState(message = viewModel.errorMessage.value) { viewModel.fetchQuizQuestions(noteId) }
            } else if (questions.isEmpty()) {
                EmptyState { viewModel.fetchQuizQuestions(noteId) }
            } else {
                val pagerState = rememberPagerState { questions.size }
                val coroutineScope = rememberCoroutineScope()

                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Câu ${pagerState.currentPage + 1} / ${questions.size}",
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
                    val quiz = questions[page]
                    QuizCard(
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

                // --- PHẦN NÚT BẤM (CỐ ĐỊNH Ở DƯỚI) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    if (!isSubmitted) {
                        val allAnswered = selectedAnswers.size == questions.size
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Thanh điều hướng câu hỏi
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                items(count = questions.size) { index ->
                                    val quizId = questions[index].id
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
                                            text = (index + 1).toString(),
                                            color = if (isAnswered) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = { 
                                    isSubmitted = true 
                                    viewModel.submitQuizScore(noteId, score, total)
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                enabled = allAnswered,
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("NỘP BÀI", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            if (!allAnswered) {
                                Text(
                                    text = "Hoàn thành ${selectedAnswers.size}/${questions.size} câu để nộp bài",
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
}

@Composable
fun QuizCard(
    quiz: QuizQuestion,
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
            // Đảm bảo Text hiển thị câu hỏi luôn có mặt
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = containerColor,
                        contentColor = contentColor
                    ),
                    border = BorderStroke(if (isThisOptionSelected || (isSubmitted && isCorrect)) 2.dp else 1.dp, borderColor),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = option, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        if (isSubmitted) {
                            if (isCorrect) Text("✓", fontWeight = FontWeight.Bold)
                            else if (isThisOptionSelected) Text("✕", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isSubmitted) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Giải thích: ${quiz.explanation}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "❌ Lỗi: $message", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text("Thử lại") }
    }
}

@Composable
fun EmptyState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "📭 Không có dữ liệu câu hỏi.")
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text("Tải lại") }
    }
}
