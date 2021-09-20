package com.example.quizcomposeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

@Preview
@Composable
fun SelectSubject( myViewModel: QuizViewModel = QuizViewModel(), navigateToDestination: (String) -> Unit = {"destination"}) {

    val downLoadText: String by myViewModel.downloadText.observeAsState("")
    val downLoadCompleted: Boolean by myViewModel.downLoadCompleted.observeAsState(false)
    val subjectSelection: QuizViewModel.QuizSubject by myViewModel.selectedSubject.observeAsState(
        initial = QuizViewModel.QuizSubject.NONE
    )

    Column {
        Text(
            text = downLoadText,
            modifier = Modifier
                .padding(10.dp)
                .weight(1F)
        )

        Text(
            fontSize = 16.sp,
            text =
                """
                Benvenuti nell'app Quiz.
                Selezionate un argomento tra quelli elencati
                per andare al quiz.
                Buon divertimento!
                """.trimIndent(),
            modifier = Modifier
                .padding(10.dp)
                .weight(3F)
        )

        Box(modifier = Modifier.weight(2F)) {
            ShowSubjects(
                subjectSelection,
                { subject -> myViewModel.setSelectedSubject(subject) }
            )
        }

        Button(
            enabled = downLoadCompleted,
            onClick = { navigateToDestination("showQuestionCard") },
            modifier = Modifier.padding(5.dp)
        ) {
            Text(text = "Vai al quiz")
        }

    }


}


@Composable
fun ShowSubjects(
    selectedSubject: QuizViewModel.QuizSubject,
    setSelectedSubject: (subject: QuizViewModel.QuizSubject) -> Unit
) {
    val subjects = mutableListOf(QuizViewModel.QuizSubject.FLAGS)
    subjects.add(QuizViewModel.QuizSubject.DOGS)

    LazyColumn {
        items(subjects.size) { index ->
            MessageRow(
                subjects[index],
                selectedSubject,
                { subject -> setSelectedSubject(subject) }
            )
        }
    }
}

@Composable
fun MessageRow(
    subject: QuizViewModel.QuizSubject,
    selectedSubject: QuizViewModel.QuizSubject,
    setSelectedSubject: (subject: QuizViewModel.QuizSubject) -> Unit
) {
    Row (modifier = Modifier
                    .selectable(
                        selected = (subject == selectedSubject),
                        onClick = { setSelectedSubject(subject) }
                    )
    ) {
        Image(
            painter = when (subject) {
                QuizViewModel.QuizSubject.FLAGS -> painterResource(id = R.drawable.globe_with_the_flags)
                QuizViewModel.QuizSubject.DOGS -> painterResource(id = R.drawable.puppy)
                else -> painterResource(id = R.drawable.man_with_question_mark)
            },
            contentDescription = "Subject image",
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .height(40.dp)
                .align(Alignment.CenterVertically)
                .weight(1F)
        )

        Text(
            text = when (subject) {
                QuizViewModel.QuizSubject.FLAGS -> "Bandiere del mondo"
                QuizViewModel.QuizSubject.DOGS -> "Razze canine"
                else -> ""
            },

            modifier = Modifier
                .weight(5F)
                .align(Alignment.CenterVertically),
            fontSize = 14.sp,
            fontWeight = if (subject == selectedSubject) FontWeight.Bold else FontWeight.Normal
        )
    }
}