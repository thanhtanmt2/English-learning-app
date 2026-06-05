package com.example.english_learning_app.ui.vocabulary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.english_learning_app.R

@Composable
fun AddEditWordScreen(
    navController: NavHostController,
    wordSetId: String,
    wordId: String? = null,
    viewModel: AddEditWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(wordSetId, wordId) {
        viewModel.load(wordSetId, wordId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = if (wordId.isNullOrBlank()) stringResource(R.string.add_edit_word_title_add) else stringResource(R.string.add_edit_word_title_edit),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.add_edit_word_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6C757D)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Text(text = stringResource(R.string.common_loading), color = Color(0xFF6C757D))
            return@Column
        }

        if (uiState.errorMessage != null) {
            Text(
                text = "${stringResource(R.string.learning_load_error)}${uiState.errorMessage}",
                color = Color(0xFFB00020),
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = uiState.word,
            onValueChange = { viewModel.updateWord(it) },
            label = { Text(stringResource(R.string.add_edit_word_word_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.meaning,
            onValueChange = { viewModel.updateMeaning(it) },
            label = { Text(stringResource(R.string.add_edit_word_meaning_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.example,
            onValueChange = { viewModel.updateExample(it) },
            label = { Text(stringResource(R.string.add_edit_word_example_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.save {
                    navController.navigate("word_list/$wordSetId") {
                        popUpTo("word_list/$wordSetId") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (uiState.isSaving) stringResource(R.string.add_edit_word_saving) else stringResource(R.string.add_edit_word_save))
        }
    }
}

