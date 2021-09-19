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
            val flags: Elements = doc.select("td:has(a.image)")
            for (flag in flags) {
                var childCounter = 0
                var url = ""
                var name = ""
                for (myChild in flag.children()) {
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
            val doc = Jsoup.connect("https://www.caniegatti.info/razze_dei_cani").get()
            val dogs: Elements = doc.select("article:has(figure)")
            for (dog in dogs) {
                val url = dog.child(0).child(0).child(0).child(0).attr("data-src").toString().substringAfter("ret_img/").dropLast(12) + ".jpg"
                val name = dog.child(1).text()

                quizList.add(QuizViewModel.MyQuestion(url, name))
            }

            //val filteredQuizList = quizList.dropLast(1) as MutableList<QuizViewModel.MyQuestion> // fake data for last 1 item
            quizList.shuffle()
            quizList
        }

    }

}