package com.example.english_learning_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.english_learning_app.ui.home.HomeScreen
import com.example.english_learning_app.ui.learning.DictationScreen
import com.example.english_learning_app.ui.learning.FlashcardScreen
import com.example.english_learning_app.ui.vocabulary.AddWordSetScreen
import com.example.english_learning_app.ui.vocabulary.AddEditWordScreen
import com.example.english_learning_app.ui.vocabulary.WordListScreen
import com.example.english_learning_app.ui.vocabulary.WordSetListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
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
        composable("grammar_list") { GrammarListScreen(navController) }
        composable("add_edit_grammar") { AddEditGrammarScreen(navController) }
        composable("grammar_quiz") { GrammarQuizScreen(navController) }
        composable("progress") { ProgressScreen(navController) }
    }
}

@Composable
private fun ScreenColumn(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun LoginScreen(navController: NavHostController) {
    ScreenColumn(title = "Login Screen") {
        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Go to Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login -> Home")
        }
    }
}

@Composable
private fun RegisterScreen(navController: NavHostController) {
    ScreenColumn(title = "Register Screen") {
        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register -> Home")
        }
    }
}

@Composable
private fun GrammarListScreen(navController: NavHostController) {
    ScreenColumn(title = "Grammar List Screen") {
        Button(
            onClick = { navController.navigate("add_edit_grammar") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add/Edit Grammar")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("grammar_quiz") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Grammar Quiz")
        }
    }
}

@Composable
private fun AddEditGrammarScreen(navController: NavHostController) {
    ScreenColumn(title = "Add/Edit Grammar Screen") {
        Button(
            onClick = {
                navController.navigate("grammar_list") {
                    popUpTo("grammar_list") { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Grammar List")
        }
    }
}

@Composable
private fun GrammarQuizScreen(navController: NavHostController) {
    ScreenColumn(title = "Grammar Quiz Screen") {
        Button(
            onClick = {
                navController.navigate("grammar_list") {
                    popUpTo("grammar_list") { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Grammar List")
        }
    }
}

@Composable
private fun ProgressScreen(navController: NavHostController) {
    ScreenColumn(title = "Progress Screen") {
        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Home")
        }
    }
}