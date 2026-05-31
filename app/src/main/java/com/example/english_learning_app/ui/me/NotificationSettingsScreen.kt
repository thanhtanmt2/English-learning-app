package com.example.english_learning_app.ui.me

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.english_learning_app.ui.auth.AuthViewModel
import com.example.english_learning_app.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = viewModel()
) {
    val user = authViewModel.currentUser.value
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(user?.id) {
        user?.id?.let { viewModel.load(it) }
    }

    LaunchedEffect(uiState.statusMessage) {
        val message = uiState.statusMessage
        if (message != null) {
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearStatus()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage
        if (message != null) {
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notification_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (user == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.notification_settings_login_required))
            }
            return@Scaffold
        }

        val settings = uiState.settings

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    stringResource(R.string.notification_settings_section_reminders),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.notification_settings_daily_reminder),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.dailyReminderEnabled,
                        onCheckedChange = viewModel::updateDailyReminderEnabled
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = settings.dailyReminderTime,
                    onValueChange = viewModel::updateDailyReminderTime,
                    label = { Text(stringResource(R.string.notification_settings_daily_time)) },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.notification_settings_review_reminder),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.reviewReminderEnabled,
                        onCheckedChange = viewModel::updateReviewReminderEnabled
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.notification_settings_streak_reminder),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.streakReminderEnabled,
                        onCheckedChange = viewModel::updateStreakReminderEnabled
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = settings.dailyReminderMessage,
                    onValueChange = viewModel::updateDailyMessage,
                    label = { Text(stringResource(R.string.notification_settings_daily_message)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = settings.reviewReminderMessage,
                    onValueChange = viewModel::updateReviewMessage,
                    label = { Text(stringResource(R.string.notification_settings_review_message)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = settings.streakReminderMessage,
                    onValueChange = viewModel::updateStreakMessage,
                    label = { Text(stringResource(R.string.notification_settings_streak_message)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.notification_settings_section_in_app),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.notification_settings_enable_in_app),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.pushNotificationEnabled,
                        onCheckedChange = viewModel::updatePushEnabled
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = settings.inAppTitle,
                    onValueChange = viewModel::updateInAppTitle,
                    label = { Text(stringResource(R.string.notification_settings_in_app_title)) },
                    leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = settings.inAppMessage,
                    onValueChange = viewModel::updateInAppMessage,
                    label = { Text(stringResource(R.string.notification_settings_in_app_message)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = viewModel::sendTestInApp,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.notification_settings_send_test_in_app))
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.notification_settings_section_email),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.notification_settings_enable_email),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.emailNotificationEnabled,
                        onCheckedChange = viewModel::updateEmailEnabled
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = settings.emailSubject,
                    onValueChange = viewModel::updateEmailSubject,
                    label = { Text(stringResource(R.string.notification_settings_email_subject)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = settings.emailMessage,
                    onValueChange = viewModel::updateEmailMessage,
                    label = { Text(stringResource(R.string.notification_settings_email_message)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = viewModel::save,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (uiState.isSaving) {
                            stringResource(R.string.notification_settings_saving)
                        } else {
                            stringResource(R.string.notification_settings_save)
                        }
                    )
                }
            }

            if (uiState.inAppNotifications.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.notification_settings_recent_in_app),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(uiState.inAppNotifications.take(5)) { item ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(item.title, style = MaterialTheme.typography.titleSmall)
                        Text(item.message, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
