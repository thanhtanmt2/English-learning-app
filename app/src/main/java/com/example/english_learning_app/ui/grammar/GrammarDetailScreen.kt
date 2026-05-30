package com.example.english_learning_app.ui.grammar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_learning_app.data.model.GrammarNote

@Composable
fun GrammarDetailScreen(
    note: GrammarNote,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextButton(onClick = onNavigateBack) {
            Text("⬅ Quay lại")
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = note.title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(text = note.category, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
        
        Spacer(modifier = Modifier.height(16.dp))

        DetailSection(title = "Cấu trúc / Công thức", content = note.formula)
        DetailSection(title = "Giải thích chi tiết", content = note.explanation)
        DetailSection(title = "Ví dụ minh họa", content = note.example)
        
        if (note.commonMistakes.isNotEmpty()) {
            DetailSection(title = "Lỗi sai thường gặp", content = note.commonMistakes)
        }
        
        note.level?.let {
            DetailSection(title = "Trình độ", content = it)
        }
    }
}

@Composable
fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = content, modifier = Modifier.padding(12.dp))
        }
    }
}
