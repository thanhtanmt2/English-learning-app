package com.example.english_learning_app.ui.me

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.english_learning_app.R
import kotlinx.coroutines.launch

@Composable
fun NotificationSettingsScreen(
    viewModel: NotificationSettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.load() }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.clearStatus()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val settings = uiState.settings

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(stringResource(R.string.notif_daily_reminder), style = MaterialTheme.typography.titleMedium)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.notif_daily_subtitle), modifier = Modifier.weight(1f))
                    Switch(
                        checked = settings.dailyReminder,
                        onCheckedChange = viewModel::updateDailyReminder
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = settings.reminderTime,
                    onValueChange = viewModel::updateReminderTime,
                    label = { Text(stringResource(R.string.notif_time_label)) },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(stringResource(R.string.notif_study_notifications), style = MaterialTheme.typography.titleMedium)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.notif_grammar_quiz), modifier = Modifier.weight(1f))
                    Switch(
                        checked = settings.quizReminders,
                        onCheckedChange = viewModel::updateQuizReminders
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.notif_progress_update), modifier = Modifier.weight(1f))
                    Switch(
                        checked = settings.progressUpdates,
                        onCheckedChange = viewModel::updateProgressUpdates
                    )
                }
            }

            item {
                // Nút test: bắn thông báo ngay lập tức để kiểm tra
                OutlinedButton(
                    onClick = viewModel::testNotification,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔔 Gửi thông báo thử ngay")
                }
            }

            item {
                Button(
                    onClick = viewModel::save,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.isSaving) stringResource(R.string.notification_settings_saving) else stringResource(R.string.notification_settings_save))
                }
            }
        }
    }
}
