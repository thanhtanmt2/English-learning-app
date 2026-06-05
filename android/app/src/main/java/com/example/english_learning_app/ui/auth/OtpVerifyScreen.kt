package com.example.english_learning_app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_learning_app.R
import com.example.english_learning_app.ui.theme.SuccessGreen

/**
 * Màn xác thực OTP, dùng chung cho 2 luồng:
 *  - mode = "register"       → nhập OTP rồi hoàn tất đăng ký
 *  - mode = "reset_password" → nhập OTP rồi đặt mật khẩu mới
 */
@Composable
fun OtpVerifyScreen(
    viewModel: AuthViewModel,
    mode: String,
    goal: String = "",
    level: String = "",
    onSuccess: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isRegisterMode = mode == "register"

    LaunchedEffect(uiState.isRegisterSuccess) {
        if (uiState.isRegisterSuccess) {
            viewModel.clearRegisterSuccess()
            viewModel.clearOtpState()
            onSuccess()
        }
    }

    LaunchedEffect(uiState.isPasswordResetSuccess) {
        if (uiState.isPasswordResetSuccess) {
            viewModel.clearPasswordResetSuccess()
            viewModel.clearOtpState()
            onSuccess()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isRegisterMode) stringResource(R.string.otp_verify_title_register) else stringResource(R.string.otp_verify_title_reset),
                fontSize = 22.sp,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.otp_sent_message, uiState.otpEmail),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) otp = it },
                label = { Text(stringResource(R.string.otp_otp_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp,
                    fontSize = 24.sp
                )
            )

            if (!isRegisterMode) {
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.otp_new_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.otp_confirm_password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.successMessage.isNotEmpty()) {
                Text(
                    text = uiState.successMessage,
                    color = SuccessGreen,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (uiState.errorMessage.isNotEmpty()) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (isRegisterMode) {
                        viewModel.registerComplete(otp, goal, level)
                    } else {
                        if (newPassword != confirmPassword) {
                            return@Button
                        }
                        viewModel.resetPassword(otp, newPassword)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && otp.length == 6 &&
                        (isRegisterMode || (newPassword.isNotBlank() && newPassword == confirmPassword))
            ) {
                Text(
                    if (uiState.isLoading) stringResource(R.string.otp_processing)
                    else if (isRegisterMode) stringResource(R.string.otp_complete_register)
                    else stringResource(R.string.otp_reset_password)
                )
            }

            if (!isRegisterMode && newPassword.isNotBlank() && newPassword != confirmPassword) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.otp_passwords_dont_match),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }
        }
    }
}
