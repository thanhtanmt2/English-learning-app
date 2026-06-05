package com.example.english_learning_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.english_learning_app.ui.auth.AuthViewModel
import com.example.english_learning_app.ui.auth.ForgotPasswordScreen
import com.example.english_learning_app.ui.auth.LoginScreen
import com.example.english_learning_app.ui.auth.OtpVerifyScreen
import com.example.english_learning_app.ui.auth.RegisterScreen
import com.example.english_learning_app.ui.grammar.AddEditGrammarScreen
import com.example.english_learning_app.ui.grammar.GrammarDetailScreen
import com.example.english_learning_app.ui.grammar.GrammarQuizScreen
import com.example.english_learning_app.ui.grammar.GrammarViewModel
import com.example.english_learning_app.ui.learning.DictationScreen
import com.example.english_learning_app.ui.learning.FlashcardScreen
import com.example.english_learning_app.ui.me.AboutScreen
import com.example.english_learning_app.ui.me.LanguageSettingsScreen
import com.example.english_learning_app.ui.me.NotificationSettingsScreen
import com.example.english_learning_app.ui.me.ProfileScreen
import com.example.english_learning_app.ui.settings.ServerSettingsScreen
import com.example.english_learning_app.ui.progress.ProgressDetailScreen
import com.example.english_learning_app.ui.progress.ProgressScreen
import com.example.english_learning_app.ui.progress.ProgressViewModel
import com.example.english_learning_app.ui.vocabulary.AddEditWordScreen
import com.example.english_learning_app.ui.vocabulary.AddWordSetScreen
import com.example.english_learning_app.ui.vocabulary.AiWordListScreen
import com.example.english_learning_app.ui.vocabulary.EditWordSetScreen
import com.example.english_learning_app.ui.vocabulary.WordListScreen
import com.example.english_learning_app.ui.vocabulary.WordQuizScreen
import com.example.english_learning_app.ui.vocabulary.WordQuizSetupScreen
import com.example.english_learning_app.ui.vocabulary.WordSetListScreen

@Composable
fun AppNavHost(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "login") {

        // ── Auth ──────────────────────────────────────────────────────────────
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToOtp = { navController.navigate("otp_verify/register") }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onOtpSent = { navController.navigate("otp_verify/reset_password") }
            )
        }
        composable(
            route = "otp_verify/{mode}",
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "register"
            val uiState by authViewModel.uiState.collectAsState()
            OtpVerifyScreen(
                viewModel = authViewModel,
                mode = mode,
                goal = uiState.pendingGoal,
                level = uiState.pendingLevel,
                onSuccess = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // ── Main shell (bottom nav) ───────────────────────────────────────────
        composable("main") {
            MainWithBottomNav(authViewModel = authViewModel, rootNavController = navController)
        }

        // ── Grammar sub-screens ───────────────────────────────────────────────
        composable(
            route = "add_edit_grammar?id={id}",
            arguments = listOf(navArgument("id") {
                nullable = true; defaultValue = null; type = NavType.StringType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val grammarViewModel: GrammarViewModel = hiltViewModel()
            AddEditGrammarScreen(navController = navController, viewModel = grammarViewModel, noteId = id)
        }
        composable("grammar_detail") {
            val note = navController.previousBackStackEntry?.savedStateHandle
                ?.get<com.example.english_learning_app.data.model.GrammarNote>("note")
            if (note != null) {
                GrammarDetailScreen(
                    note = note,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuiz = { id -> navController.navigate("grammar_quiz/$id") }
                )
            }
        }
        composable(
            route = "grammar_quiz/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val grammarViewModel: GrammarViewModel = hiltViewModel()
            GrammarQuizScreen(
                viewModel = grammarViewModel,
                noteId = noteId,
            )
        }

        // ── Progress sub-screens ──────────────────────────────────────────────
        composable("progress") {
            val progressViewModel: ProgressViewModel = hiltViewModel()
            ProgressScreen(
                viewModel = progressViewModel,
                onNavigateToDetail = { date -> navController.navigate("progress_detail/$date") }
            )
        }
        composable("progress_detail/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            val progressViewModel: ProgressViewModel = hiltViewModel()
            ProgressDetailScreen(
                date = date,
                viewModel = progressViewModel,
            )
        }

        // ── Vocabulary sub-screens ────────────────────────────────────────────
        composable(
            route = "word_set_list?refresh={refresh}",
            arguments = listOf(navArgument("refresh") { type = NavType.BoolType; defaultValue = false })
        ) { backStackEntry ->
            val refresh = backStackEntry.arguments?.getBoolean("refresh") ?: false
            WordSetListScreen(navController = navController, refresh = refresh)
        }
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
            val wordSetId = backStackEntry.arguments?.getString("wordSetId")
            FlashcardScreen(
                navController = navController,
                wordSetId = wordSetId,
                onWordSetChanged = { newId ->
                    // Bug 1 fix: replace word_list cũ trong back stack bằng word_list của WS mới
                    navController.navigate("word_list/$newId") {
                        popUpTo("word_list/{wordSetId}") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "dictation?wordSetId={wordSetId}",
            arguments = listOf(navArgument("wordSetId") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val wordSetId = backStackEntry.arguments?.getString("wordSetId")
            DictationScreen(
                navController = navController,
                wordSetId = wordSetId,
                onWordSetChanged = { newId ->
                    navController.navigate("word_list/$newId") {
                        popUpTo("word_list/{wordSetId}") { inclusive = true }
                    }
                }
            )
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
            WordQuizScreen(navController = navController, wordSetIdsParam = wordSetIds, countParam = count)
        }
        composable("add_word_set") { AddWordSetScreen(navController) }
        composable("ai_word_list") { AiWordListScreen(navController) }
        composable(
            route = "edit_word_set/{wordSetId}",
            arguments = listOf(navArgument("wordSetId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditWordSetScreen(
                navController = navController,
                wordSetId = backStackEntry.arguments?.getString("wordSetId") ?: ""
            )
        }

        // ── Me sub-screens ────────────────────────────────────────────────────
        composable("edit_profile") {
            ProfileScreen(viewModel = authViewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("language_settings") {
            LanguageSettingsScreen()
        }
        composable("notification_settings") {
            NotificationSettingsScreen(
                viewModel = hiltViewModel()
            )
        }
        composable("about_app") {
            AboutScreen()
        }
        composable("server_settings") {
            ServerSettingsScreen()
        }
    }
}
