package com.example.english_learning_app.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.english_learning_app.R
import com.example.english_learning_app.ui.auth.AuthViewModel
import com.example.english_learning_app.ui.grammar.GrammarListScreen
import com.example.english_learning_app.ui.grammar.GrammarViewModel
import com.example.english_learning_app.ui.home.HomeScreen
import com.example.english_learning_app.ui.me.MeScreen
import com.example.english_learning_app.ui.vocabulary.WordSetListScreen

enum class BottomNavItem(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector
) {
    HOME("home", R.string.bottom_nav_home, Icons.Default.Home),
    VOCABULARY("vocabulary", R.string.bottom_nav_vocabulary, Icons.Default.MenuBook),
    GRAMMAR("grammar", R.string.bottom_nav_grammar, Icons.Default.Edit),
    ME("me", R.string.bottom_nav_me, Icons.Default.Person)
}

@Composable
fun MainWithBottomNav(authViewModel: AuthViewModel, rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    val grammarViewModel: GrammarViewModel = hiltViewModel()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(tonalElevation = 4.dp) {
                BottomNavItem.values().forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelResId)) },
                        label = { Text(stringResource(item.labelResId)) },
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.HOME.route) {
                HomeScreen(navController = rootNavController, authViewModel = authViewModel)
            }
            composable(BottomNavItem.VOCABULARY.route) {
                WordSetListScreen(navController = rootNavController, refresh = false)
            }
            composable(BottomNavItem.GRAMMAR.route) {
                // Bug #6: Refresh danh sách mỗi lần user quay lại tab Grammar
                // (sau khi add/edit/delete ở màn hình ngoài bottom nav)
                val grammarRoute = BottomNavItem.GRAMMAR.route
                LaunchedEffect(currentRoute) {
                    if (currentRoute == grammarRoute) {
                        grammarViewModel.fetchGrammarNotes()
                    }
                }
                GrammarListScreen(
                    viewModel = grammarViewModel,
                    onNavigateBack = {},
                    onNavigateToAdd = { rootNavController.navigate("add_edit_grammar") },
                    onNavigateToEdit = { id -> rootNavController.navigate("add_edit_grammar?id=$id") },
                    onNavigateToDetail = { note ->
                        rootNavController.currentBackStackEntry?.savedStateHandle?.set("note", note)
                        rootNavController.navigate("grammar_detail")
                    }
                )
            }
            composable(BottomNavItem.ME.route) {
                MeScreen(
                    authViewModel = authViewModel,
                    onNavigateToEditProfile = { rootNavController.navigate("edit_profile") },
                    onNavigateToLanguage = { rootNavController.navigate("language_settings") },
                    onNavigateToNotifications = { rootNavController.navigate("notification_settings") },
                    onNavigateToAbout = { rootNavController.navigate("about_app") },
                    onNavigateToServerSettings = { rootNavController.navigate("server_settings") },
                    onLogout = {
                        rootNavController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
