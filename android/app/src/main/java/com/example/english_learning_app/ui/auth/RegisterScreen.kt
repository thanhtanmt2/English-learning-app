package com.example.english_learning_app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_learning_app.R
import com.example.english_learning_app.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToOtp: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    val goalOptions = listOf(
        "Giao tiếp hàng ngày" to stringResource(R.string.register_goal_daily),
        "Luyện thi IELTS/TOEIC" to stringResource(R.string.register_goal_ielts),
        "Tiếng Anh công việc" to stringResource(R.string.register_goal_work),
        "Du học" to stringResource(R.string.register_goal_study_abroad),
        "Sở thích cá nhân" to stringResource(R.string.register_goal_hobby)
    )
    val levelOptions = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    var selectedGoalValue by remember { mutableStateOf(goalOptions.first().first) }
    var selectedLevel by remember { mutableStateOf("A1") }
    var goalExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }

    val selectedGoalLabel = goalOptions.firstOrNull { it.first == selectedGoalValue }?.second ?: selectedGoalValue

    LaunchedEffect(Unit) {
        viewModel.clearMessages()
    }

    // Khi OTP được gửi thành công → chuyển sang màn OTP
    LaunchedEffect(uiState.otpSent) {
        if (uiState.otpSent) {
            onNavigateToOtp()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.register_title), fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::updateName,
            label = { Text(stringResource(R.string.register_name_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::updateEmail,
            label = { Text(stringResource(R.string.login_email_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::updatePassword,
            label = { Text(stringResource(R.string.login_password_label)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = goalExpanded,
            onExpandedChange = { goalExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedGoalLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.register_goal_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = goalExpanded,
                onDismissRequest = { goalExpanded = false }
            ) {
                goalOptions.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedGoalValue = value
                            goalExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = levelExpanded,
            onExpandedChange = { levelExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedLevel,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.register_level_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = levelExpanded,
                onDismissRequest = { levelExpanded = false }
            ) {
                levelOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedLevel = option
                            levelExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.successMessage.isNotEmpty()) {
            Text(text = uiState.successMessage, color = SuccessGreen)
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (uiState.errorMessage.isNotEmpty()) {
            Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { viewModel.sendRegisterOtp(selectedGoalValue, selectedLevel) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text(if (uiState.isLoading) stringResource(R.string.register_sending_code) else stringResource(R.string.register_send_code))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text(stringResource(R.string.register_has_account))
        }
    }
}
