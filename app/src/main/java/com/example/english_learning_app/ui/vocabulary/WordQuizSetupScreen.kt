package com.example.english_learning_app.ui.vocabulary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlin.math.roundToInt

@Composable
fun WordQuizSetupScreen(
    navController: NavHostController,
    viewModel: WordSetListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedSetIds = remember { mutableStateListOf<String>() }
    var questionCount by remember { mutableIntStateOf(15) }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Tao bai kiem tra", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { navController.popBackStack() }) {
                Text(text = "Huy")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Chon nhieu word set va so cau hoi tu 5 den 100.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6C757D)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "So cau: $questionCount", fontWeight = FontWeight.SemiBold)
        Slider(
            value = questionCount.toFloat(),
            onValueChange = { value ->
                questionCount = value.roundToInt().coerceIn(5, 100)
            },
            valueRange = 5f..100f
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Word sets", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

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

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.wordSets) { wordSet ->
                val wordSetIdStr = wordSet.id.toString()
                val isChecked = selectedSetIds.contains(wordSetIdStr)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    if (!selectedSetIds.contains(wordSetIdStr)) {
                                        selectedSetIds.add(wordSetIdStr)
                                    }
                                } else {
                                    selectedSetIds.remove(wordSetIdStr)
                                }
                            }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = wordSet.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "${wordSet.learnedWords}/${wordSet.totalWords} words learned",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6C757D)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        val canStart = selectedSetIds.isNotEmpty()
        Button(
            onClick = {
                val ids = selectedSetIds.joinToString(",")
                navController.navigate("word_quiz?wordSetIds=$ids&count=$questionCount")
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = canStart
        ) {
            Text(text = "Bat dau", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        if (!canStart) {
            Text(
                text = "Hay chon it nhat 1 word set.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFB00020),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
