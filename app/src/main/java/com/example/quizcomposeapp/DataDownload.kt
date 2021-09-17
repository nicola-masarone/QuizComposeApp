package com.example.quizcomposeapp

import org.jsoup.Jsoup
import org.jsoup.select.Elements

fun dataDownload(
    subject: QuizViewModel.QuizSubject,
    quizList: MutableList<QuizViewModel.MyQuestion>
):MutableList<QuizViewModel.MyQuestion> {

    return when (subject) {
        QuizViewModel.QuizSubject.FLAGS -> {
            val doc = Jsoup.connect("https://it.wikipedia.org/wiki/Lista_di_bandiere_nazionali").get()
            val altFlags: Elements = doc.select("td:has(a.image)")
            for (altFlag in altFlags) {
                var childCounter = 0
                var url = ""
                var name = ""
                for (myChild in altFlag.children()) {
                    when (childCounter) {
                        0 -> {
                            url = if (myChild.childNode(0).childNodeSize() == 0) {
                                "https:" + myChild.childNode(0).attr("src").toString()
                                    .replace("/200px", "/800px")
                            } else {
                                "https:" + myChild.childNode(0).childNode(0).attr("src").toString()
                                    .replace("/200px", "/800px")
                            }
                        }
                        2 -> name = myChild.text()
                        3 -> name = name + " (" + myChild.text() + ")"
                    }
                    childCounter++
                }
                quizList.add(QuizViewModel.MyQuestion(url, name))
            }
            val filteredQuizList = quizList.dropLast(1) as MutableList<QuizViewModel.MyQuestion> // fake data for last 1 item
            filteredQuizList.shuffle()
            filteredQuizList
        }

        QuizViewModel.QuizSubject.DOGS -> {

            quizList
        }

    }

}