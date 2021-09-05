package com.example.quizcomposeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter

@Composable
fun ShowQuestionCard(
    myViewModel: QuizViewModel,
    dialog: DialogViewModel,
    navigateToDestination: (String) -> Unit = {"destination"}
) {
    val pts: Int by myViewModel.points.observeAsState(0)
    val questionInd: Int by myViewModel.questionIndex.observeAsState(0)
    val myOptions: List<String> by myViewModel.options.observeAsState(listOf())
    val dialogVisibility: Boolean by dialog.visibility.observeAsState(false)
    val selectedOption:String by myViewModel.selOption.observeAsState("")

    QuestionCard(
        imageUrl = myViewModel.myQuestions[questionInd].picUrl,
        options = myOptions,
        optionOk = myViewModel.myQuestions[questionInd].name,
        question = "A quale nazione appartiene la bandiera in figura?\nRisposte esatte: $pts",
        changePoints = { newPts -> myViewModel.updatePoints(pts + newPts)},
        skipQuestion = { myViewModel.nextQuestion() },
        dialog,
        setOption = { option -> myViewModel.setSelOption(option) },
        selOption = selectedOption
    )

    if (dialogVisibility)
        ShowDialog(
            dialog,
            { myViewModel.nextQuestion() },
            { text -> myViewModel.setSelOption(text) }
        )
}

@Composable
fun QuestionCard (
    imageUrl: String,
    options: List<String>,
    optionOk: String,
    question: String,
    changePoints: (Int) -> Unit,
    skipQuestion: () -> Unit,
    dialog: DialogViewModel,
    setOption: (String) -> Unit,
    selOption: String
) {

    Column {
        Row(modifier = Modifier.padding(8.dp)) {

            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "Quiz image",
                modifier = Modifier
                    .border(1.dp, Color.Black).padding(1.dp)
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
                                selected = (text == selOption),
                                onClick = { setOption(text) },
                                role = Role.RadioButton
                            )
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selOption),
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
                    setOption("")
                    skipQuestion()
                },
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) { Text("Salta") }
            Button(
                onClick = {
                    if (selOption == optionOk) {
                        dialog.setText("Risposta esatta!")
                        dialog.setIcon(Icons.Filled.ThumbUp)
                        dialog.setVisibility(true)
                        changePoints(1)
                    }
                    else {
                        dialog.setText("Errore!\nLa risposta corretta era:\n$optionOk")
                        dialog.setIcon(Icons.Filled.Error)
                        dialog.setVisibility(true)
                        changePoints(-1)
                    }
                },
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) { Text("Rispondi") }
        }
    }

}


@Composable
fun ShowDialog(
    dialog: DialogViewModel,
    nextQuestion: () -> Unit,
    setOption: (String) -> Unit
) {
    Dialog(onDismissRequest = {
        dialog.setVisibility(false)
        nextQuestion()
        setOption("")
    }) {
        Box(
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(dialog.icon.value!!, contentDescription = "Localized description")
                Text(
                    dialog.text.value!!,
                    Modifier.padding(6.dp)
                )
            }
        }
    }
}

