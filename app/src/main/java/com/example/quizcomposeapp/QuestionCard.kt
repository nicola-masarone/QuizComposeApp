package com.example.quizcomposeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter

@Composable
fun ShowQuestionCard(myViewModel: QuizViewModel, navigateToDestination: (String) -> Unit = {"destination"}) {
    val pts: Int by myViewModel.points.observeAsState(0)
    val questionInd: Int by myViewModel.questionIndex.observeAsState(0)
    val myOptions: List<String> by myViewModel.options.observeAsState(listOf())
    val myWrongAnswer: Boolean by myViewModel.wrongAnswer.observeAsState(false)
    val myGoodAnswer: Boolean by myViewModel.goodAnswer.observeAsState(false)


    QuestionCard(
        imageUrl = myViewModel.myQuestions[questionInd].picUrl,
        options = myOptions,
        optionOk = myViewModel.myQuestions[questionInd].name,
        question = "A quale nazione appartiene la bandiera in figura?\nRisposte esatte: $pts",
        changePoints = { newPts -> myViewModel.updatePoints(pts + newPts)},
        nextQuestion = { myViewModel.nextQuestion() },
        skipQuestion = { myViewModel.nextQuestion() },
        setWrongAnswer = { myViewModel.setWrongAnswer() },
        setGoodAnswer =  { myViewModel.setGoodAnswer() }
    )

    if (myWrongAnswer) {
        ErrorDialog(
            resetWrongAnswer = { myViewModel.resetWrongAnswer() },
            correctAnswer = myViewModel.myQuestions[questionInd].name
        )
    }

    if (myGoodAnswer) {
        OkDialog(
            resetGoodAnswer = { myViewModel.resetGoodAnswer() },
            comment = "Risposta corretta!"
        )
    }

}

@Preview
@Composable
fun QuestionCard (
    imageUrl: String = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/03/Flag_of_Italy.svg/150px-Flag_of_Italy.svg.png",
    options: List<String> = listOf("Italia", "Francia", "Spagna", "Germania", "Regno Unito"),
    optionOk: String = options[0],
    question: String = "A quale nazione appartiene la bandiera rappresentata nell'immagine?",
    changePoints: (Int) -> Unit = { },
    nextQuestion: () -> Unit = { },
    skipQuestion: () -> Unit = { },
    setWrongAnswer: () -> Unit = { },
    setGoodAnswer:  () -> Unit = { }
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf("") }

    Column {
        Row(modifier = Modifier.padding(8.dp)) {

            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "Quiz image",
                modifier = Modifier
                    .weight(1F)
                    .align(Alignment.CenterVertically)
            )

            // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
            Column(
                Modifier
                    .selectableGroup()
                    .weight(1F)
                    .align(Alignment.CenterVertically) ) {
                options.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
                                role = Role.RadioButton
                            )
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null // null recommended for accessibility with screenreaders
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.body1.merge(),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        Text(
            text = question,
            modifier = Modifier
                .padding(8.dp)
                .weight(1F)
        )

        Row {
            Button(
                onClick = {
                    onOptionSelected("")
                    skipQuestion()
                },
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) { Text("Salta") }
            Button(
                onClick = {
                    if (selectedOption == optionOk) {
                        setGoodAnswer()
                        changePoints(1)
                        //nextQuestion()
                    }
                    else {
                        setWrongAnswer()
                        changePoints(-1)
                    }
                    onOptionSelected("")
                },
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) { Text("Rispondi") }
        }
    }

}

@Preview
@Composable
fun ErrorDialog(
    resetWrongAnswer: () -> Unit = { },
    correctAnswer: String = "Italia"
) {
    Dialog(onDismissRequest = resetWrongAnswer) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Box(
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Error, contentDescription = "Localized description")
                Text(
                    "La risposta corretta era:\n$correctAnswer",
                    Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun OkDialog(
    resetGoodAnswer: () -> Unit = { },
    comment: String = "Ben fatto!"
) {
    Dialog(onDismissRequest = resetGoodAnswer) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Box(
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.ThumbUp, contentDescription = "Localized description")
                Text(
                    "Risposta esatta!",
                    Modifier.padding(6.dp)
                )
            }
        }
    }
}