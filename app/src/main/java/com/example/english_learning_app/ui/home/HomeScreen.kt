package com.example.english_learning_app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
                        SurfaceChip("🎯 ${user.goal}")
                    }
                    if (!user?.level.isNullOrBlank()) {
                        SurfaceChip("📊 ${user.level}")
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

            // ── Biểu đồ Thời gian học (Daily Activity) ─────────────────────
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
                        text = "⏱ Daily Activity (Phút)",
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

            Spacer(modifier = Modifier.height(16.dp))

            // ── Retention Rate ─────────────────────────────────
            val retentionRate = if (history.isEmpty()) 0
            else (history.sumOf { it.quizScore } * 100) / (history.size * 5)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🧠 Retention Rate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "$retentionRate%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = when {
                                retentionRate >= 80 -> Color(0xFF4CAF50)
                                retentionRate >= 50 -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tỷ lệ nhớ đúng trong các buổi ôn tập gần đây",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = retentionRate / 100f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = when {
                                            retentionRate >= 80 -> listOf(Color(0xFF43A047), Color(0xFF66BB6A))
                                            retentionRate >= 50 -> listOf(Color(0xFFF57C00), Color(0xFFFFB74D))
                                            else -> listOf(Color(0xFFD32F2F), Color(0xFFEF5350))
                                        }
                                    )
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when {
                            retentionRate >= 80 -> "🌟 Tuyệt vời! Bạn đang nhớ tốt"
                            retentionRate >= 50 -> "💪 Khá ổn, hãy ôn tập thêm"
                            else -> "📚 Cần cố gắng ôn tập nhiều hơn"
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Level Estimation ────────────────────────────────
            val userLevel = user?.level ?: "A1"
            val (_, levelColor, levelDesc) = when (userLevel) {
                "A1", "A2" -> Triple("Beginner 🟢", Color(0xFF4CAF50), "Bạn đang ở mức cơ bản. Hãy học đều đặn mỗi ngày!")
                "B1", "B2" -> Triple("Intermediate 🟡", Color(0xFFFF9800), "Bạn đã có nền tảng tốt. Tiếp tục phát triển!")
                "C1", "C2" -> Triple("Advanced 🔴", Color(0xFFF44336), "Tuyệt vời! Bạn đang ở mức nâng cao.")
                else -> Triple("Beginner 🟢", Color(0xFF4CAF50), "Hãy bắt đầu hành trình học tập!")
            }

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
                        text = "🎓 Level Estimation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 3 mức Beginner / Intermediate / Advanced
                        listOf(
                            Triple("A1", "A2", "Beginner"),
                            Triple("B1", "B2", "Intermediate"),
                            Triple("C1", "C2", "Advanced")
                        ).forEach { (l1, l2, label) ->
                            val isActive = userLevel == l1 || userLevel == l2
                            val color = when (label) {
                                "Beginner" -> Color(0xFF4CAF50)
                                "Intermediate" -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isActive) 52.dp else 40.dp)
                                        .clip(CircleShape)
                                        .background(if (isActive) color else color.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (label == "Beginner") "🟢" else if (label == "Intermediate") "🟡" else "🔴",
                                        fontSize = if (isActive) 22.sp else 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isActive) color else MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = levelColor.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = levelDesc,
                            fontSize = 13.sp,
                            color = levelColor,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
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
                        text = "📋 Lịch sử học tập",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                history.forEach { record ->
                    Spacer(modifier = Modifier.height(8.dp))
                    HomeHistoryItem(record = record, onClick = { navController.navigate("progress_detail/${record.date}") })
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
    // Sắp xếp tăng dần theo ngày và lấy đúng 7 ngày gần nhất
    val chartData = records.sortedBy { it.date }.takeLast(7)
    val maxTime = chartData.maxOfOrNull { it.studyTimeMinutes.toFloat() }?.coerceAtLeast(1f) ?: 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom
    ) {
        chartData.forEach { record ->
            val barHeightRatio = (record.studyTimeMinutes.toFloat() / maxTime)
            val barHeight = 100.dp * barHeightRatio

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.height(140.dp)
            ) {
                // Nhãn số phút
                Text(
                    text = "${record.studyTimeMinutes}m",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Cột biểu đồ
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(barHeight.coerceAtLeast(4.dp))
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
                            )
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Nhãn ngày (chỉ lấy MM-DD)
                val dateLabel = if (record.date.length >= 10) record.date.substring(5) else record.date
                Text(
                    text = dateLabel,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun HomeHistoryItem(record: ProgressRecord, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
