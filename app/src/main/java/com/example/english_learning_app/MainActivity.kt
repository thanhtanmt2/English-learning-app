package com.example.english_learning_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

// Auth
import com.example.english_learning_app.ui.auth.AuthViewModel
import com.example.english_learning_app.ui.auth.LoginScreen
import com.example.english_learning_app.ui.auth.RegisterScreen
import com.example.english_learning_app.ui.auth.ProfileScreen

// Grammar
import com.example.english_learning_app.ui.grammar.*

// Progress
import com.example.english_learning_app.ui.progress.ProgressScreen
import com.example.english_learning_app.ui.progress.ProgressViewModel
import com.example.english_learning_app.ui.progress.ProgressDetailScreen

// Home & Vocabulary (tan_frontend)
import com.example.english_learning_app.ui.home.HomeScreen
import com.example.english_learning_app.ui.learning.DictationScreen
import com.example.english_learning_app.ui.learning.FlashcardScreen
import com.example.english_learning_app.ui.vocabulary.AddEditWordScreen
import com.example.english_learning_app.ui.vocabulary.AddWordSetScreen
import com.example.english_learning_app.ui.vocabulary.EditWordSetScreen
import com.example.english_learning_app.ui.vocabulary.WordListScreen
import com.example.english_learning_app.ui.vocabulary.WordQuizScreen
import com.example.english_learning_app.ui.vocabulary.WordQuizSetupScreen
import com.example.english_learning_app.ui.vocabulary.WordSetListScreen

// Me
import com.example.english_learning_app.ui.me.MeScreen

// ===================== Bottom Nav Items =====================
enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Default.Home),
    VOCABULARY("vocabulary", "Từ vựng", Icons.Default.MenuBook),
    GRAMMAR("grammar", "Ngữ pháp", Icons.Default.Edit),
    ME("me", "Tôi", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            AppNavHost(navController = navController, authViewModel = authViewModel)
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "login") {

        // ===================== AUTH (không có bottom bar) =====================
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ===================== MAIN (có Bottom Nav Bar) =====================
        composable("main") {
            MainWithBottomNav(authViewModel = authViewModel, rootNavController = navController)
        }

        // ===================== Sub-screens (không có bottom bar) =====================

        // Grammar sub-screens
        composable(
            route = "add_edit_grammar?id={id}",
            arguments = listOf(navArgument("id") {
                nullable = true; defaultValue = null; type = NavType.StringType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val grammarViewModel: GrammarViewModel = viewModel()
            AddEditGrammarScreen(
                navController = navController,
                viewModel = grammarViewModel,
                noteId = id
            )
        }
        composable("grammar_detail") {
            val note = navController.previousBackStackEntry?.savedStateHandle
                ?.get<com.example.english_learning_app.data.model.GrammarNote>("note")
            if (note != null) {
                GrammarDetailScreen(note = note, onNavigateBack = { navController.popBackStack() })
            }
        }
        composable("grammar_quiz") {
            val grammarViewModel: GrammarViewModel = viewModel()
            GrammarQuizScreen(
                viewModel = grammarViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Progress sub-screen
        composable("progress") {
            val progressViewModel: ProgressViewModel = viewModel()
            ProgressScreen(
                viewModel = progressViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { date -> navController.navigate("progress_detail/$date") }
            )
        }
        composable("progress_detail/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            val progressViewModel: ProgressViewModel = viewModel()
            ProgressDetailScreen(
                date = date,
                viewModel = progressViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Vocabulary sub-screens
        composable(
            route = "word_list/{wordSetId}",
            arguments = listOf(navArgument("wordSetId") { type = NavType.StringType })
        ) { backStackEntry ->
            WordListScreen(navController, backStackEntry.arguments?.getString("wordSetId") ?: "")
        }
        composable(
            route = "add_edit_word/{wordSetId}",
            arguments = listOf(navArgument("wordSetId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddEditWordScreen(navController, backStackEntry.arguments?.getString("wordSetId") ?: "", null)
        }
        composable(
            route = "add_edit_word/{wordSetId}/{wordId}",
            arguments = listOf(
                navArgument("wordSetId") { type = NavType.StringType },
                navArgument("wordId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AddEditWordScreen(
                navController,
                backStackEntry.arguments?.getString("wordSetId") ?: "",
                backStackEntry.arguments?.getString("wordId")
            )
        }
        composable(
            route = "flashcard?wordSetId={wordSetId}",
            arguments = listOf(navArgument("wordSetId") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            FlashcardScreen(navController, backStackEntry.arguments?.getString("wordSetId"))
        }
        composable(
            route = "dictation?wordSetId={wordSetId}",
            arguments = listOf(navArgument("wordSetId") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            DictationScreen(navController, backStackEntry.arguments?.getString("wordSetId"))
        }
        composable("word_quiz_setup") {
            WordQuizSetupScreen(navController = navController)
        }
        composable(
            route = "word_quiz?wordSetIds={wordSetIds}&count={count}",
            arguments = listOf(
                navArgument("wordSetIds") { type = NavType.StringType; defaultValue = "" },
                navArgument("count") { type = NavType.IntType; defaultValue = 15 }
            )
        ) { backStackEntry ->
            val wordSetIds = backStackEntry.arguments?.getString("wordSetIds") ?: ""
            val count = backStackEntry.arguments?.getInt("count") ?: 15
            WordQuizScreen(
                navController = navController,
                wordSetIdsParam = wordSetIds,
                countParam = count
            )
        }
        composable("add_word_set") { AddWordSetScreen(navController) }
        composable(
            route = "edit_word_set/{wordSetId}",
            arguments = listOf(navArgument("wordSetId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditWordSetScreen(
                navController = navController,
                wordSetId = backStackEntry.arguments?.getString("wordSetId") ?: ""
            )
        }

        // Profile edit
        composable("edit_profile") {
            ProfileScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// ===================== Main Shell với Bottom Nav =====================
@Composable
fun MainWithBottomNav(authViewModel: AuthViewModel, rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    val grammarViewModel: GrammarViewModel = viewModel()

    // Lắng nghe route hiện tại để tô sáng đúng tab
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(tonalElevation = 4.dp) {
                BottomNavItem.values().forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                // Tránh chồng chất stack khi bấm nhiều lần
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
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
            // Tab HOME
            composable(BottomNavItem.HOME.route) {
                HomeScreen(navController = rootNavController, authViewModel = authViewModel)
            }

            // Tab VOCABULARY
            composable(BottomNavItem.VOCABULARY.route) {
                WordSetListScreen(navController = rootNavController, refresh = false)
            }

            // Tab GRAMMAR
            composable(BottomNavItem.GRAMMAR.route) {
                GrammarListScreen(
                    viewModel = grammarViewModel,
                    onNavigateBack = {},  // Không cần back vì đây là tab root
                    onNavigateToAdd = { rootNavController.navigate("add_edit_grammar") },
                    onNavigateToEdit = { id -> rootNavController.navigate("add_edit_grammar?id=$id") },
                    onNavigateToQuiz = { rootNavController.navigate("grammar_quiz") },
                    onNavigateToDetail = { note ->
                        rootNavController.currentBackStackEntry?.savedStateHandle?.set("note", note)
                        rootNavController.navigate("grammar_detail")
                    }
                )
            }

            // Tab ME
            composable(BottomNavItem.ME.route) {
                MeScreen(
                    authViewModel = authViewModel,
                    onNavigateToEditProfile = { rootNavController.navigate("edit_profile") },
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
