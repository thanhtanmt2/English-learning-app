package com.example.english_learning_app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.english_learning_app.BuildConfig
import com.example.english_learning_app.R
import com.example.english_learning_app.data.local.ServerPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun ServerSettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var urlInput by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val msgEmpty = stringResource(R.string.server_settings_url_empty_error)
    val msgInvalid = stringResource(R.string.server_settings_url_invalid_error)
    val msgSaved = stringResource(R.string.server_settings_saved)

    LaunchedEffect(Unit) {
        urlInput = ServerPreferences.baseUrlFlow(context).first()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Dns,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = stringResource(R.string.me_server_settings_title),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.server_settings_description, BuildConfig.DEFAULT_BASE_URL),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                label = { Text(stringResource(R.string.server_settings_base_url_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )
            Button(
                onClick = {
                    scope.launch {
                        isSaving = true
                        val url = urlInput.trim()
                        if (url.isBlank()) {
                            snackbarHostState.showSnackbar(msgEmpty)
                        } else if (!url.startsWith("http")) {
                            snackbarHostState.showSnackbar(msgInvalid)
                        } else {
                            ServerPreferences.setBaseUrl(context, url)
                            snackbarHostState.showSnackbar(msgSaved)
                        }
                        isSaving = false
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.server_settings_save_button))
                }
            }
        }
    }
}
