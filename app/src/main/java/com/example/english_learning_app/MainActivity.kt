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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.english_learning_app.ui.auth.AuthViewModel
import com.example.english_learning_app.ui.auth.LoginScreen
import com.example.english_learning_app.ui.auth.RegisterScreen
import com.example.english_learning_app.ui.auth.ProfileScreen
import com.example.english_learning_app.ui.grammar.*
import com.example.english_learning_app.ui.progress.ProgressScreen
import com.example.english_learning_app.ui.progress.ProgressViewModel

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
    NavHost(navController = navController, startDestination = "login") {
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
        composable("home") { HomeScreen(navController, authViewModel) }
        
        composable("grammar_list") { 
            val grammarViewModel: GrammarViewModel = viewModel()
            GrammarListScreen(
                viewModel = grammarViewModel,
                onNavigateToAdd = { navController.navigate("add_edit_grammar") },
                onNavigateToEdit = { id -> navController.navigate("add_edit_grammar?id=$id") },
                onNavigateToQuiz = { navController.navigate("grammar_quiz") },
                onNavigateToDetail = { note -> 
                    // Lưu note vào state của ViewModel hoặc truyền qua argument
                    // Ở đây tạm dùng static state hoặc truyền đơn giản
                    navController.currentBackStackEntry?.savedStateHandle?.set("note", note)
                    navController.navigate("grammar_detail")
                }
            )
        }
        composable(
            route = "add_edit_grammar?id={id}",
            arguments = listOf(navArgument("id") { nullable = true; defaultValue = null; type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val grammarViewModel: GrammarViewModel = viewModel()
            AddEditGrammarScreen(
                navController = navController,
                viewModel = grammarViewModel,
                noteId = id
            )
        }
        composable("grammar_quiz") { 
            val grammarViewModel: GrammarViewModel = viewModel()
            GrammarQuizScreen(
                viewModel = grammarViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("grammar_detail") {
            val note = navController.previousBackStackEntry?.savedStateHandle?.get<com.example.english_learning_app.data.model.GrammarNote>("note")
            if (note != null) {
                GrammarDetailScreen(note = note, onNavigateBack = { navController.popBackStack() })
            }
        }
        composable("progress") { 
            val progressViewModel: ProgressViewModel = viewModel()
            ProgressScreen(
                viewModel = progressViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun HomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val user = authViewModel.currentUser.value
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top Profile Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("profile") }
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = user?.name ?: "Người dùng",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Mục tiêu: ${user?.goal ?: "Chưa chọn"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Text(
            text = "Menu Chính",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("grammar_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ngữ pháp (Grammar)")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("progress") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Tiến độ học tập")
        }
    }
}
