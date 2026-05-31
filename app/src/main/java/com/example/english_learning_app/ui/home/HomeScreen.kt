package com.example.english_learning_app.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.english_learning_app.data.model.ProgressOverview
import com.example.english_learning_app.data.model.WordSet

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel = viewModel()) {
    val state = viewModel.uiState
    val uiState by state.collectAsState()

    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }

    val contentOffset by animateDpAsState(
        targetValue = if (revealed) 0.dp else 18.dp,
        animationSpec = tween(durationMillis = 450)
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(durationMillis = 450)
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF6EFE8),
                            Color(0xFFFDE2C6),
                            Color(0xFFF4D6E0)
                        )
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB7E4C7).copy(alpha = 0.5f))
            )
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD6A5).copy(alpha = 0.6f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(top = contentOffset)
                    .background(Color.Transparent)
                    .shadow(0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeHeader(
                    userName = uiState.data?.user?.name ?: "Nguoi hoc",
                    isLoading = uiState.isLoading
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = "Khong the tai du lieu: ${uiState.errorMessage}",
                        color = Color(0xFFB00020),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val progress = uiState.data?.progress
                val firstWordSet = uiState.data?.wordSets?.firstOrNull()

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7EE)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tien do hom nay",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2B2D42)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatChip(title = "Streak", value = progressValue(progress?.streak, "days"))
                            StatChip(title = "Goal", value = goalValue(progress))
                            StatChip(title = "Done", value = doneValue(progress))
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF4FF)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tiep tuc bai hoc",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1D3557)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = wordSetTitle(firstWordSet),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF457B9D)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { navController.navigate("word_set_list") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A9D8F)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Mo danh sach tu")
                        }
                    }
                }

                Text(
                    text = "Bat dau nhanh",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2B2D42)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ActionCard(
                            title = "Flashcard",
                            subtitle = "On tap tu vung",
                            accent = Color(0xFFF4A261),
                            onClick = { navController.navigate("flashcard") },
                            modifier = Modifier.weight(1f)
                        )
                        ActionCard(
                            title = "Dictation",
                            subtitle = "Nghe va viet",
                            accent = Color(0xFF3A86FF),
                            onClick = { navController.navigate("dictation") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ActionCard(
                            title = "Grammar",
                            subtitle = "Tong hop ngu phap",
                            accent = Color(0xFF06D6A0),
                            onClick = { navController.navigate("grammar_list") },
                            modifier = Modifier.weight(1f)
                        )
                        ActionCard(
                            title = "Progress",
                            subtitle = "Bao cao hoc tap",
                            accent = Color(0xFF8338EC),
                            onClick = { navController.navigate("progress") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.08f * (1f - contentAlpha)))
            )
        }
    }
}

@Composable
private fun HomeHeader(userName: String, isLoading: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isLoading) "Dang tai du lieu..." else "Chao mung $userName",
            fontSize = 24.sp,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B2D42)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Hom nay ban muon hoc gi?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF4A4E69)
        )
    }
}

private fun progressValue(value: Int?, suffix: String): String {
    return if (value == null) "--" else "$value $suffix"
}

private fun goalValue(progress: ProgressOverview?): String {
    return if (progress == null) "--" else "${progress.totalWords} words"
}

private fun doneValue(progress: ProgressOverview?): String {
    return if (progress == null) "--" else "${progress.learnedWords} words"
}

private fun wordSetTitle(wordSet: WordSet?): String {
    return if (wordSet == null) "Word set: --" else "Word set: ${wordSet.name}"
}

@Composable
private fun StatChip(title: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F3F8))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = title, fontSize = 11.sp, color = Color(0xFF6C757D))
        Text(text = value, fontWeight = FontWeight.SemiBold, color = Color(0xFF2B2D42))
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .height(96.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.18f))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, color = Color(0xFF2B2D42))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6C757D))
            }
        }
    }
}
