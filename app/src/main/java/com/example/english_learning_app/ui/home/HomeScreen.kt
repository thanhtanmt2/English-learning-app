package com.example.english_learning_app.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.data.model.ProgressRecord
import com.example.english_learning_app.ui.auth.AuthViewModel
import com.example.english_learning_app.ui.progress.ProgressViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    progressViewModel: ProgressViewModel = viewModel()
) {
    val user = authViewModel.currentUser.value
    val overview = progressViewModel.progressOverview.value
    val history = progressViewModel.progressRecords.value

    LaunchedEffect(Unit) {
        progressViewModel.fetchProgress()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header chào mừng ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 28.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Xin chào! 👋",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Text(
                            text = user?.name ?: "Người học",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Chips mục tiêu và trình độ
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!user?.goal.isNullOrBlank()) {
                        SurfaceChip("🎯 ${user?.goal}")
                    }
                    if (!user?.level.isNullOrBlank()) {
                        SurfaceChip("📊 ${user?.level}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Dashboard: Streak + Accuracy ─────────────────────────────
        if (progressViewModel.isLoading.value) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Whatshot,
                    iconColor = Color(0xFFFF5722),
                    label = "Streak",
                    value = "${overview?.streak ?: 0} Ngày"
                )
                HomeStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AdsClick,
                    iconColor = Color(0xFF4CAF50),
                    label = "Accuracy",
                    value = "${overview?.accuracyRate ?: 0}%"
                )
                HomeStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.MenuBook,
                    iconColor = Color(0xFF2196F3),
                    label = "Đã học",
                    value = "${overview?.totalWordsLearned ?: 0} từ"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Biểu đồ Thời gian học ─────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⏱ Thời gian học (Phút)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (history.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Chưa có dữ liệu", color = MaterialTheme.colorScheme.secondary)
                        }
                    } else {
                        HomeBarChart(records = history)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Lịch sử gần đây (3 ngày) ─────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 Gần đây",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    TextButton(onClick = { navController.navigate("progress") }) {
                        Text("Xem tất cả →", fontSize = 13.sp)
                    }
                }

                history.take(3).forEach { record ->
                    Spacer(modifier = Modifier.height(8.dp))
                    HomeHistoryItem(record = record)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SurfaceChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun HomeStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HomeBarChart(records: List<ProgressRecord>) {
    val chartData = records.take(7).reversed()
    val maxTime = chartData.maxOfOrNull { it.studyTimeMinutes.toFloat() }?.coerceAtLeast(1f) ?: 1f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
    ) {
        val barCount = chartData.size
        val barWidth = 28.dp.toPx()
        val spacing = (size.width - barWidth * barCount) / (barCount + 1)

        chartData.forEachIndexed { index, record ->
            val barHeight = (record.studyTimeMinutes.toFloat() / maxTime) * size.height
            val x = spacing + index * (barWidth + spacing)
            val y = size.height - barHeight

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6)),
                    startY = y,
                    endY = size.height
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
    }
}

@Composable
private fun HomeHistoryItem(record: ProgressRecord) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = record.date, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    text = "${record.wordsLearned} từ • ${record.studyTimeMinutes} phút",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = "✓ ${record.quizScore}/5",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                fontSize = 13.sp
            )
        }
    }
}
