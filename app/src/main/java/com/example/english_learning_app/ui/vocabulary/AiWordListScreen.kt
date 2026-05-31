package com.example.english_learning_app.ui.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.data.model.AiGeneratedWord

// ─── Màu sắc chủ đạo ───────────────────────────────────────────────────────
private val GradientStart = Color(0xFF6C63FF)
private val GradientEnd   = Color(0xFFB06AB3)
private val CardBg        = Color(0xFFF8F7FF)
private val ChipSelected  = Color(0xFF6C63FF)
private val ChipUnsel     = Color(0xFFEEEDF8)
private val DeleteRed     = Color(0xFFE53935)
private val GoldAccent    = Color(0xFFF7B731)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiWordListScreen(
    navController: NavHostController,
    viewModel: AiWordListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "AI Word Generator",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart
                )
            )
        },
        containerColor = Color(0xFFF3F2FF)
    ) { paddingValues ->
        if (uiState.result == null) {
            // ── Bước 1: Nhập chủ đề ──────────────────────────────────────────
            InputStep(
                paddingValues = paddingValues,
                uiState = uiState,
                onTopicChange = viewModel::updateTopic,
                onWordCountChange = viewModel::updateWordCount,
                onGenerate = viewModel::generateWordList
            )
        } else {
            // ── Bước 2: Preview & lưu ────────────────────────────────────────
            PreviewStep(
                paddingValues = paddingValues,
                uiState = uiState,
                onNameChange = viewModel::updateResultName,
                onDescChange = viewModel::updateResultDescription,
                onRemoveWord = viewModel::removeWord,
                onReset = viewModel::resetResult,
                onSave = {
                    viewModel.saveAll {
                        navController.navigate("word_set_list?refresh=true") {
                            popUpTo("ai_word_list") { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Bước 1 – Nhập Topic
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun InputStep(
    paddingValues: PaddingValues,
    uiState: AiWordListUiState,
    onTopicChange: (String) -> Unit,
    onWordCountChange: (Int) -> Unit,
    onGenerate: () -> Unit
) {
    val wordCountOptions = listOf(5, 10, 15, 20)

    // Spinner animation khi đang generate
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(GradientStart, GradientEnd))
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Tạo Word List bằng AI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Nhập chủ đề, AI sẽ tạo toàn bộ từ vựng\nkèm nghĩa, ví dụ và phiên âm cho bạn!",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Topic input
        item {
            Column {
                Text(
                    text = "Chủ đề học từ vựng",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF3D3D3D)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.topic,
                    onValueChange = onTopicChange,
                    placeholder = {
                        Text(
                            "VD: Business English, Travel, Technology...",
                            color = Color(0xFFAAABBE)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GradientStart,
                        unfocusedBorderColor = Color(0xFFD0CFF5),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        // Word count selector
        item {
            Column {
                Text(
                    text = "Số lượng từ",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF3D3D3D)
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    wordCountOptions.forEach { count ->
                        val selected = uiState.wordCount == count
                        FilterChip(
                            selected = selected,
                            onClick = { onWordCountChange(count) },
                            label = {
                                Text(
                                    text = "$count từ",
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) Color.White else Color(0xFF5A5A7A)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GradientStart,
                                containerColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = GradientStart,
                                borderColor = Color(0xFFD0CFF5)
                            )
                        )
                    }
                }
            }
        }

        // Example chips gợi ý
        item {
            Column {
                Text(
                    text = "Gợi ý nhanh",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF3D3D3D)
                )
                Spacer(Modifier.height(10.dp))
                val suggestions = listOf(
                    "Business English", "Travel", "Technology",
                    "Food & Cooking", "Sports", "Medical",
                    "IELTS Academic", "Daily Conversation"
                )
                // 2 rows
                suggestions.chunked(4).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        row.forEach { s ->
                            SuggestionChip(text = s, onClick = { onTopicChange(s) })
                        }
                    }
                }
            }
        }

        // Error message
        if (uiState.errorMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⚠️ ${uiState.errorMessage}",
                        color = DeleteRed,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Generate button
        item {
            Button(
                onClick = onGenerate,
                enabled = !uiState.isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                if (!uiState.isGenerating)
                                    listOf(GradientStart, GradientEnd)
                                else
                                    listOf(Color(0xFFB0AEDE), Color(0xFFCCAACC))
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isGenerating) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(rotation)
                            )
                            Text(
                                text = "AI đang tạo từ vựng...",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "✨ Generate với AI",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Bước 2 – Preview & Lưu
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PreviewStep(
    paddingValues: PaddingValues,
    uiState: AiWordListUiState,
    onNameChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onRemoveWord: (Int) -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit
) {
    val result = uiState.result ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(GradientStart, GradientEnd))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "AI đã tạo ${result.words.size} từ vựng",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GradientStart
                        )
                    }
                    Spacer(Modifier.height(14.dp))

                    Text(text = "Tên bộ từ", fontSize = 12.sp, color = Color(0xFF888888))
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = result.name,
                        onValueChange = onNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GradientStart,
                            unfocusedBorderColor = Color(0xFFDDDDEE)
                        ),
                        singleLine = true,
                        trailingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(16.dp))
                        }
                    )
                    Spacer(Modifier.height(10.dp))

                    Text(text = "Mô tả", fontSize = 12.sp, color = Color(0xFF888888))
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = result.description,
                        onValueChange = onDescChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GradientStart,
                            unfocusedBorderColor = Color(0xFFDDDDEE)
                        ),
                        minLines = 2
                    )

                    if (result.tags.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            result.tags.take(5).forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFEEEDF8)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        fontSize = 11.sp,
                                        color = GradientStart,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Error
        if (uiState.errorMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⚠️ ${uiState.errorMessage}",
                        color = DeleteRed,
                        modifier = Modifier.padding(14.dp),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Section title
        item {
            Text(
                text = "Danh sách từ vựng",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF3D3D3D)
            )
        }

        // Word cards
        itemsIndexed(result.words) { index, word ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                AiWordCard(
                    word = word,
                    index = index,
                    onRemove = { onRemoveWord(index) }
                )
            }
        }

        // Action buttons
        item {
            Spacer(Modifier.height(4.dp))
            // Save button
            Button(
                onClick = onSave,
                enabled = !uiState.isSaving && result.words.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                if (!uiState.isSaving) listOf(GradientStart, GradientEnd)
                                else listOf(Color(0xFFB0AEDE), Color(0xFFCCAACC))
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isSaving) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Đang lưu...", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            "💾 Lưu Word Set (${result.words.size} từ)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Re-generate button
            TextButton(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GradientStart, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Tạo lại với chủ đề khác",
                    color = GradientStart,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Word Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AiWordCard(
    word: AiGeneratedWord,
    index: Int,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Index badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEEDF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GradientStart
                    )
                }
                Spacer(Modifier.width(10.dp))
                // Word + part of speech
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = word.word,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF2D2D2D)
                    )
                    if (word.partOfSpeech.isNotBlank()) {
                        Text(
                            text = word.partOfSpeech,
                            fontSize = 11.sp,
                            color = GradientStart,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
                // Pronunciation
                if (word.pronunciation.isNotBlank()) {
                    Text(
                        text = word.pronunciation,
                        fontSize = 12.sp,
                        color = Color(0xFF888888),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                // Remove button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Xóa từ",
                        tint = Color(0xFFCCCCDD),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Meaning
            Text(
                text = word.meaning,
                fontSize = 14.sp,
                color = Color(0xFF444455),
                fontWeight = FontWeight.Medium
            )

            // Example
            if (word.example.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F4FF))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "\"${word.example}\"",
                        fontSize = 12.sp,
                        color = Color(0xFF666680),
                        fontStyle = FontStyle.Italic,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Suggestion Chip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SuggestionChip(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        modifier = Modifier
            .border(1.dp, Color(0xFFD0CFF5), RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF5A5A7A),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
