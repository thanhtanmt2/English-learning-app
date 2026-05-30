package com.example.english_learning_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Auth (Nhut_frontend)
import com.example.english_learning_app.ui.auth.AuthViewModel
import com.example.english_learning_app.ui.auth.LoginScreen
import com.example.english_learning_app.ui.auth.RegisterScreen
import com.example.english_learning_app.ui.auth.ProfileScreen

// Grammar (Nhut_frontend)
import com.example.english_learning_app.ui.grammar.*

// Progress (Nhut_frontend)
import com.example.english_learning_app.ui.progress.ProgressScreen
import com.example.english_learning_app.ui.progress.ProgressViewModel

// Home & Vocabulary (tan_frontend)
import com.example.english_learning_app.ui.home.HomeScreen
import com.example.english_learning_app.ui.learning.DictationScreen
import com.example.english_learning_app.ui.learning.FlashcardScreen
import com.example.english_learning_app.ui.vocabulary.AddEditWordScreen
import com.example.english_learning_app.ui.vocabulary.AddWordSetScreen
import com.example.english_learning_app.ui.vocabulary.WordListScreen
import com.example.english_learning_app.ui.vocabulary.WordSetListScreen

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
private fun AppNavHost(navController: NavHostController, authViewModel: AuthViewModel) {
    val grammarViewModel: GrammarViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {

        // ===================== AUTH (Nhut_frontend) =====================
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    navController.navigate("home") {
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
        composable("profile") {
            ProfileScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ===================== HOME (kết hợp cả hai) =====================
        composable("home") {
            HomeScreen(navController = navController)
        }

        // ===================== VOCABULARY (tan_frontend) =====================
        composable(
            route = "word_set_list?refresh={refresh}",
            arguments = listOf(navArgument("refresh") {
                type = NavType.BoolType
                defaultValue = false
            })
        ) { backStackEntry ->
            val refresh = backStackEntry.arguments?.getBoolean("refresh") ?: false
            WordSetListScreen(navController, refresh)
        }
        composable("add_word_set") { AddWordSetScreen(navController) }
        composable(
            route = "word_list/{wordSetId}",
            arguments = listOf(navArgument("wordSetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val wordSetId = backStackEntry.arguments?.getString("wordSetId") ?: ""
            WordListScreen(navController, wordSetId)
        }
        composable(
            route = "add_edit_word/{wordSetId}",
            arguments = listOf(navArgument("wordSetId") { type = NavType.StringType })
        ) { backStackEntry ->
            val wordSetId = backStackEntry.arguments?.getString("wordSetId") ?: ""
            AddEditWordScreen(navController, wordSetId, null)
        }
        composable(
            route = "add_edit_word/{wordSetId}/{wordId}",
            arguments = listOf(
                navArgument("wordSetId") { type = NavType.StringType },
                navArgument("wordId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val wordSetId = backStackEntry.arguments?.getString("wordSetId") ?: ""
            val wordId = backStackEntry.arguments?.getString("wordId")
            AddEditWordScreen(navController, wordSetId, wordId)
        }

        // ===================== LEARNING (tan_frontend) =====================
        composable(
            route = "flashcard?wordSetId={wordSetId}",
            arguments = listOf(navArgument("wordSetId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val wordSetId = backStackEntry.arguments?.getString("wordSetId")
            FlashcardScreen(navController, wordSetId)
        }
        composable(
            route = "dictation?wordSetId={wordSetId}",
            arguments = listOf(navArgument("wordSetId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val wordSetId = backStackEntry.arguments?.getString("wordSetId")
            DictationScreen(navController, wordSetId)
        }

        // ===================== GRAMMAR (Nhut_frontend) =====================
        composable("grammar_list") {
            GrammarListScreen(
                viewModel = grammarViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate("add_edit_grammar") },
                onNavigateToEdit = { id -> navController.navigate("add_edit_grammar?id=$id") },
                onNavigateToQuiz = { navController.navigate("grammar_quiz") },
                onNavigateToDetail = { note ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("note", note)
                    navController.navigate("grammar_detail")
                }
            )
        }
        composable(
            route = "add_edit_grammar?id={id}",
            arguments = listOf(navArgument("id") {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            AddEditGrammarScreen(
                navController = navController,
                viewModel = grammarViewModel,
                noteId = id
            )
        }
        composable("grammar_quiz") {
            GrammarQuizScreen(
                viewModel = grammarViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("grammar_detail") {
            val note = navController.previousBackStackEntry?.savedStateHandle
                ?.get<com.example.english_learning_app.data.model.GrammarNote>("note")
            if (note != null) {
                GrammarDetailScreen(note = note, onNavigateBack = { navController.popBackStack() })
            }
        }

        // ===================== PROGRESS (Nhut_frontend) =====================
        composable("progress") {
            val progressViewModel: ProgressViewModel = viewModel()
            ProgressScreen(
                viewModel = progressViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { date ->
                    navController.navigate("progress_detail/$date")
                }
            )
        }
        composable("progress_detail/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextButton(onClick = { navController.popBackStack() }) { Text("⬅ Quay lại") }
                    Text("Chi tiết ngày: $date", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Chức năng đang phát triển...")
                }
            }
        }
    }
}
