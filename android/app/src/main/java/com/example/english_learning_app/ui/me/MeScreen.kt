package com.example.english_learning_app.ui.me

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.english_learning_app.R
import com.example.english_learning_app.data.local.ProfilePreferences
import com.example.english_learning_app.data.local.ThemePreferences
import com.example.english_learning_app.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun MeScreen(
    authViewModel: AuthViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToServerSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.currentUser
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isDarkMode by ThemePreferences.darkModeFlow(context).collectAsState(initial = false)
    val avatarUri by ProfilePreferences.avatarUriFlow(context).collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(top = 48.dp, bottom = 28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
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
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = user?.name ?: stringResource(R.string.me_default_user),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user?.email ?: "",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel(stringResource(R.string.me_section_account))
        SettingItem(
            icon = Icons.Default.Edit,
            title = stringResource(R.string.me_edit_profile_title),
            subtitle = stringResource(R.string.me_edit_profile_subtitle),
            onClick = onNavigateToEditProfile
        )

        Spacer(modifier = Modifier.height(12.dp))

        SectionLabel(stringResource(R.string.me_section_settings))

        // Dark mode toggle
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.me_dark_mode), fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Text(text = if (isDarkMode) stringResource(R.string.me_enabled) else stringResource(R.string.me_disabled), fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { enabled ->
                        scope.launch { ThemePreferences.setDarkMode(context, enabled) }
                    }
                )
            }
        }

        SettingItem(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.me_notifications_title),
            subtitle = stringResource(R.string.me_notifications_subtitle),
            onClick = onNavigateToNotifications
        )
        SettingItem(
            icon = Icons.Default.Language,
            title = stringResource(R.string.me_language_title),
            subtitle = stringResource(R.string.me_language_subtitle),
            onClick = onNavigateToLanguage
        )
        SettingItem(
            icon = Icons.Default.Dns,
            title = stringResource(R.string.me_server_settings_title),
            subtitle = stringResource(R.string.me_server_settings_subtitle),
            onClick = onNavigateToServerSettings
        )
        SettingItem(
            icon = Icons.Default.Info,
            title = stringResource(R.string.me_about_title),
            subtitle = stringResource(R.string.me_about_subtitle),
            onClick = onNavigateToAbout
        )

        Spacer(modifier = Modifier.height(12.dp))

        SectionLabel(stringResource(R.string.me_section_other))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { authViewModel.logout(); onLogout() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Text(text = stringResource(R.string.me_logout), fontWeight = FontWeight.SemiBold, color = Color(0xFFD32F2F), fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun SettingItem(icon: ImageVector, title: String, subtitle: String = "", onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                if (subtitle.isNotEmpty()) {
                    Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
        }
    }
}
