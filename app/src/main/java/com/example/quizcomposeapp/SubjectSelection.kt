package com.example.quizcomposeapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SelectSubject( myViewModel: QuizViewModel = QuizViewModel(), navigateToDestination: (String) -> Unit = {"destination"}) {

    val downLoadText: String by myViewModel.downloadText.observeAsState("")
    val downLoadCompleted: Boolean by myViewModel.downLoadCompleted.observeAsState(false)

    Column {
        Text(text = downLoadText, modifier = Modifier.weight(1F))

        Button(
            enabled = !downLoadCompleted,
            onClick = { myViewModel.download() }
        ) {
            Text(text = "Download data")
        }

        Button(
            enabled = downLoadCompleted,
            onClick = { navigateToDestination("showQuestionCard") }
        ) {
            Text(text = "Navigate to <<Question Card>>")
        }
    }
}
