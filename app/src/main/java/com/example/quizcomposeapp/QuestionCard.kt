package com.example.quizcomposeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun ShowQuestionCard(myViewModel: QuizViewModel, navigateToDestination: (String) -> Unit = {"destination"}) {
    val pts: Int by myViewModel.points.observeAsState(0)
    val questionInd: Int by myViewModel.questionIndex.observeAsState(0)
    val myOptions: List<String> by myViewModel.options.observeAsState(listOf())

    QuestionCard(
        imageUrl = myViewModel.myQuestions[questionInd].picUrl,
        options = myOptions,
        optionOk = myViewModel.myQuestions[questionInd].name,
        question = "A quale nazione appartiene la bandiera in figura?\nRisposte esatte: $pts",
        changePoints = { newPts -> myViewModel.updatePoints(pts + newPts)},
        skipQuestion = { myViewModel.nextQuestion() }
    )
}

@Preview
@Composable
fun QuestionCard (
    imageUrl: String = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/03/Flag_of_Italy.svg/150px-Flag_of_Italy.svg.png",
    options: List<String> = listOf("Italia", "Francia", "Spagna", "Germania", "Regno Unito"),
    optionOk: String = options[0],
    question: String = "A quale nazione appartiene la bandiera rappresentata nell'immagine?",
    changePoints: (Int) -> Unit = { },
    skipQuestion: () -> Unit = { }
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
                    if (selectedOption == optionOk) { changePoints(1) }
                    else { changePoints(-1) }
                    onOptionSelected("")
                },
                modifier = Modifier
                    .weight(1F)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) { Text("Rispondi") }
        }
    }

}