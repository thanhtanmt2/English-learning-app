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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.english_learning_app.R

@Composable
fun AddWordSetScreen(
    navController: NavHostController,
    viewModel: AddWordSetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = stringResource(R.string.add_word_set_title), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.add_word_set_description),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6C757D)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.errorMessage != null) {
            Text(
                text = stringResource(R.string.add_word_set_save_error, uiState.errorMessage ?: ""),
                color = Color(0xFFB00020),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::updateName,
            label = { Text(stringResource(R.string.add_word_set_name_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::updateDescription,
            label = { Text(stringResource(R.string.add_word_set_description_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.tags,
            onValueChange = viewModel::updateTags,
            label = { Text(stringResource(R.string.add_word_set_tags_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.save {
                    navController.navigate("word_set_list?refresh=true") {
                        popUpTo("word_set_list?refresh=false") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (uiState.isSaving) stringResource(R.string.add_word_set_saving) else stringResource(R.string.add_word_set_save))
        }
    }
}
