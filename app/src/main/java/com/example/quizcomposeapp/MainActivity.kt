package com.example.quizcomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizcomposeapp.ui.theme.QuizComposeAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myViewModel: QuizViewModel by viewModels()
        val dialogViewModel: DialogViewModel by viewModels()

        setContent {
            QuizComposeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MyApp(
                        myViewModel,
                        dialogViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MyApp(
    myViewModel: QuizViewModel,
    dialogViewModel: DialogViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "selectSubject") {
        composable(route = "selectSubject") { SelectSubject(myViewModel) { destination -> navController.navigate(destination) } }
        composable(route = "showQuestionCard") { ShowQuestionCard(myViewModel, dialogViewModel) { destination -> navController.navigate(destination) } }
    }
}

