package com.example.english_learning_app.ui.me

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.english_learning_app.R
import com.example.english_learning_app.data.local.ProfilePreferences
import com.example.english_learning_app.ui.auth.AuthViewModel
import com.example.english_learning_app.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

private val GOAL_OPTIONS = listOf(
    "Giao tiếp hàng ngày",
    "Thi IELTS/TOEIC",
    "Công việc",
    "Du học",
    "Sở thích"
)

private val LEVEL_OPTIONS = listOf("A1", "A2", "B1", "B2", "C1", "C2")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit  // still needed for auto-redirect when user is null
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.currentUser
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (user == null) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    var name by remember { mutableStateOf(user.name) }
    var selectedGoal by remember { mutableStateOf(user.goal ?: GOAL_OPTIONS[0]) }
    var selectedLevel by remember { mutableStateOf(user.level ?: LEVEL_OPTIONS[0]) }
    var goalExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }

    val avatarUri by ProfilePreferences.avatarUriFlow(context).collectAsState(initial = null)

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch { ProfilePreferences.setAvatarUri(context, uri) }
        }
    }

    LaunchedEffect(uiState.isUpdateSuccess) {
        if (uiState.isUpdateSuccess) {
            viewModel.clearUpdateSuccess()
            onNavigateBack()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar picker
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = stringResource(R.string.me_avatar_cd),
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.me_avatar_cd),
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // Camera badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = stringResource(R.string.profile_pick_image_cd),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Text(
                text = stringResource(R.string.profile_change_avatar_hint),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.profile_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Email (read-only)
            OutlinedTextField(
                value = user.email,
                onValueChange = {},
                label = { Text(stringResource(R.string.login_email_label)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                singleLine = true
            )

            // Goal dropdown
            ExposedDropdownMenuBox(
                expanded = goalExpanded,
                onExpandedChange = { goalExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedGoal,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.profile_goal_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = goalExpanded,
                    onDismissRequest = { goalExpanded = false }
                ) {
                    GOAL_OPTIONS.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { selectedGoal = option; goalExpanded = false }
                        )
                    }
                }
            }

            // Level dropdown
            ExposedDropdownMenuBox(
                expanded = levelExpanded,
                onExpandedChange = { levelExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedLevel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.profile_level_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = levelExpanded,
                    onDismissRequest = { levelExpanded = false }
                ) {
                    LEVEL_OPTIONS.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { selectedLevel = option; levelExpanded = false }
                        )
                    }
                }
            }

            if (uiState.successMessage.isNotEmpty()) {
                Text(text = uiState.successMessage, color = SuccessGreen)
            }
            if (uiState.errorMessage.isNotEmpty()) {
                Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.updateProfile(name.trim(), selectedGoal, selectedLevel) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.add_edit_word_save))
                }
            }
        }
    }
}
