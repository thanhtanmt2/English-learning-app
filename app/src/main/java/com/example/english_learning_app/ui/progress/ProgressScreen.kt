package com.example.english_learning_app.ui.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.fetchProgress()
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "TIẾN ĐỘ HỌC TẬP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        if (viewModel.isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.errorMessage.value.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = viewModel.errorMessage.value, color = MaterialTheme.colorScheme.error)
            }
        } else {
            val overview = viewModel.progressOverview.value
            val history = viewModel.progressRecords.value

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Streak",
                            value = "${overview?.streak ?: 0} Ngày",
                            icon = Icons.Default.Whatshot,
                            color = Color(0xFFFF5722)
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Accuracy",
                            value = "${overview?.accuracyRate ?: 0}%",
                            icon = Icons.Default.AdsClick,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Thời gian học (Phút)", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        StudyTimeBarChart(records = history)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Chi tiết các ngày", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(history) { record ->
                    HistoryItem(record = record, onClick = { onNavigateToDetail(record.date) })
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HistoryItem(record: com.example.english_learning_app.data.model.ProgressRecord, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = record.date, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "Đã học: ${record.wordsLearned} từ • ${record.studyTimeMinutes} phút",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Đúng: ${record.quizScore}/5", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun StudyTimeBarChart(records: List<com.example.english_learning_app.data.model.ProgressRecord>) {
    if (records.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
            Text("Chưa có dữ liệu")
        }
        return
    }

    // Lấy 7 ngày gần nhất và đảo ngược để ngày cũ bên trái, ngày mới bên phải
    val displayRecords = records.takeLast(7).reversed()
    val maxTime = displayRecords.maxOfOrNull { it.studyTimeMinutes.toFloat() }?.coerceAtLeast(1f) ?: 1f

    val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(top = 24.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        val barCount = displayRecords.size
        val barWidth = 28.dp.toPx()
        val spacing = (size.width - (barWidth * barCount)) / (barCount + 1)
        val textColor = Color.Gray.toArgb()

        displayRecords.forEachIndexed { index, record ->
            val barHeight = (record.studyTimeMinutes.toFloat() / maxTime) * (size.height - 20.dp.toPx())
            val x = spacing + index * (barWidth + spacing)
            val y = size.height - 20.dp.toPx() - barHeight

            // Vẽ cột
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Vẽ tiêu đề ngày (T2, T3...)
            drawContext.canvas.nativeCanvas.drawText(
                days[index % 7],
                x + barWidth / 2,
                size.height,
                android.graphics.Paint().apply {
                    color = textColor
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            
            // Vẽ số phút trên đầu cột
            drawContext.canvas.nativeCanvas.drawText(
                "${record.studyTimeMinutes}",
                x + barWidth / 2,
                y - 5.dp.toPx(),
                android.graphics.Paint().apply {
                    color = textColor
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}
