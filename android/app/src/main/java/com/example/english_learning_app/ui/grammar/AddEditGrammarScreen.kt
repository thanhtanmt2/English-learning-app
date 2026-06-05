package com.example.english_learning_app.ui.grammar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.english_learning_app.R

@Composable
fun AddEditGrammarScreen(
    navController: NavHostController,
    viewModel: GrammarViewModel,
    noteId: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    // Khi mở màn hình edit, gọi loadNoteForEdit để fetch + populate state trong VM
    LaunchedEffect(noteId) {
        if (noteId != null) {
            viewModel.loadNoteForEdit(noteId)
        } else {
            viewModel.clearEditingState()
        }
    }

    // Dọn dẹp editing state khi rời màn hình
    DisposableEffect(Unit) {
        onDispose { viewModel.clearEditingState() }
    }

    // Điều hướng về sau khi lưu thành công
    LaunchedEffect(uiState.isAddSuccess) {
        if (uiState.isAddSuccess) {
            viewModel.resetAddSuccess()
            navController.popBackStack()
        }
    }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = uiState.hasUnsavedChanges) {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.grammar_add_edit_confirm_exit_title)) },
            text = { Text(stringResource(R.string.grammar_add_edit_confirm_exit_body)) },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; navController.popBackStack() }) {
                    Text(stringResource(R.string.grammar_add_edit_exit), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text(stringResource(R.string.grammar_add_edit_stay)) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (noteId != null) stringResource(R.string.grammar_add_edit_title_edit) else stringResource(R.string.grammar_add_edit_title_add),
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Hiện loading spinner khi đang load data cho edit
        if (noteId != null && !uiState.isEditDataLoaded && uiState.grammarNotes.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
        }

        OutlinedTextField(
            value = uiState.editingTitle,
            onValueChange = { viewModel.updateEditingTitle(it) },
            label = { Text(stringResource(R.string.grammar_add_edit_title_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.editingCategory,
            onValueChange = { viewModel.updateEditingCategory(it) },
            label = { Text(stringResource(R.string.grammar_add_edit_category_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.editingFormula,
            onValueChange = { viewModel.updateEditingFormula(it) },
            label = { Text(stringResource(R.string.grammar_add_edit_formula_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.editingExplanation,
            onValueChange = { viewModel.updateEditingExplanation(it) },
            label = { Text(stringResource(R.string.grammar_add_edit_explanation_label)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.editingExample,
            onValueChange = { viewModel.updateEditingExample(it) },
            label = { Text(stringResource(R.string.grammar_add_edit_example_label)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.editingCommonMistakes,
            onValueChange = { viewModel.updateEditingCommonMistakes(it) },
            label = { Text(stringResource(R.string.grammar_add_edit_mistakes_label)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Text(stringResource(R.string.grammar_add_edit_saving), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        } else if (uiState.errorMessage.isNotEmpty()) {
            Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (noteId != null) {
                    viewModel.updateGrammarNote(
                        noteId,
                        uiState.editingTitle,
                        uiState.editingCategory,
                        uiState.editingFormula,
                        uiState.editingExplanation,
                        uiState.editingExample,
                        uiState.editingCommonMistakes
                    )
                } else {
                    viewModel.addGrammarNote(
                        uiState.editingTitle,
                        uiState.editingCategory,
                        uiState.editingFormula,
                        uiState.editingExplanation,
                        uiState.editingExample,
                        uiState.editingCommonMistakes
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.editingTitle.isNotBlank() && !uiState.isLoading
        ) {
            Text(if (noteId != null) stringResource(R.string.grammar_add_edit_update_button) else stringResource(R.string.grammar_add_edit_save_button))
        }
    }
}
