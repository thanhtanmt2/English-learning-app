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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
        composable("word_set_list") { WordSetListScreen(navController) }
        composable("word_list") { WordListScreen(navController) }
        composable("add_edit_word") { AddEditWordScreen(navController) }
        composable("flashcard") { FlashcardScreen(navController) }
        composable("dictation") { DictationScreen(navController) }
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
            onClick = { navController.navigate("home") },
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
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register -> Home")
        }
    }
}

@Composable
private fun HomeScreen(navController: NavHostController) {
    ScreenColumn(title = "Home Screen") {
        Button(
            onClick = { navController.navigate("word_set_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Vocabulary: Word Sets")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("flashcard") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Learning: Flashcard")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("dictation") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Learning: Dictation")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("grammar_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Grammar")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("progress") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Progress")
        }
    }
}

@Composable
private fun WordSetListScreen(navController: NavHostController) {
    ScreenColumn(title = "Word Set List Screen") {
        Button(
            onClick = { navController.navigate("word_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Open Word List")
        }
    }
}

@Composable
private fun WordListScreen(navController: NavHostController) {
    ScreenColumn(title = "Word List Screen") {
        Button(
            onClick = { navController.navigate("add_edit_word") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add/Edit Word")
        }
    }
}

@Composable
private fun AddEditWordScreen(navController: NavHostController) {
    ScreenColumn(title = "Add/Edit Word Screen") {
        Button(
            onClick = { navController.navigate("word_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Word List")
        }
    }
}

@Composable
private fun FlashcardScreen(navController: NavHostController) {
    ScreenColumn(title = "Flashcard Screen") {
        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Home")
        }
    }
}

@Composable
private fun DictationScreen(navController: NavHostController) {
    ScreenColumn(title = "Dictation Screen") {
        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Home")
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
            onClick = { navController.navigate("grammar_list") },
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
            onClick = { navController.navigate("grammar_list") },
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
            onClick = { navController.navigate("home") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Home")
        }
    }
}