package com.example.english_learning_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.english_learning_app.data.local.LanguagePreferences
import com.example.english_learning_app.data.local.ThemePreferences
import com.example.english_learning_app.navigation.AppNavHost
import com.example.english_learning_app.ui.auth.AuthViewModel
import com.example.english_learning_app.ui.theme.English_learning_appTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // Launcher xử lý kết quả xin quyền — Android sẽ gọi lại đây sau khi người dùng chọn Allow/Deny
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // isGranted = true: người dùng đã cho phép bắn thông báo
        // isGranted = false: người dùng từ chối — thông báo sẽ không hiện
    }

    // Hàm kiểm tra và xin quyền thông báo nếu chưa có
    private fun askNotificationPermission() {
        // Chỉ cần xin quyền trên Android 13 (API 33) trở lên
        // Các phiên bản cũ hơn không cần xin — thông báo luôn được phép mặc định
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Chưa có quyền → hiện popup hỏi người dùng
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            // Nếu đã có quyền rồi thì không làm gì thêm
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Xin quyền thông báo ngay khi app mở lần đầu
        askNotificationPermission()
        setContent {
            val context = LocalContext.current

            val currentLocales = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            val initialLang = if (currentLocales.isNotEmpty()) currentLocales else LanguagePreferences.DEFAULT_LANGUAGE

            val languageTag by LanguagePreferences.languageFlow(context)
                .collectAsState(initial = initialLang)

            val isDarkMode by ThemePreferences.darkModeFlow(context)
                .collectAsState(initial = false)

            LaunchedEffect(languageTag) {
                val current = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                if (current != languageTag) {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(languageTag)
                    )
                }
            }

            val navController = rememberNavController()
            val authViewModel: AuthViewModel = hiltViewModel()
            English_learning_appTheme(darkTheme = isDarkMode, dynamicColor = false) {
                AppNavHost(navController = navController, authViewModel = authViewModel)
            }
        }
    }
}
