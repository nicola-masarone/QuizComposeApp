package com.example.quizcomposeapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import kotlin.random.Random

class QuizViewModel: ViewModel() {
    // LiveData holds state which is observed by the UI
    // (state flows down from ViewModel)
    private val _points = MutableLiveData(0)
    val points: LiveData<Int> = _points

    // updateCount is an event we're defining that the UI can invoke
    // (events flow up from UI)
    fun updatePoints(newPts: Int) {
        _points.value = newPts
        nextQuestion()
    }

    private val _questionIndex = MutableLiveData(0)
    val questionIndex: LiveData<Int> = _questionIndex

    fun nextQuestion() {
        _questionIndex.value = (_questionIndex.value?.plus(1))?.rem(myQuestions.size)
        _options.value = buildOptions()
    }

    private val _options = MutableLiveData<List<String>>()
    val options: LiveData<List<String>> = _options
    private var optionsNum = 4  // Alternative options for each question

    fun buildOptions(): List<String> {
        val options = mutableListOf<String>()
        var candidateOption: String
        repeat(optionsNum) {
            do {
                candidateOption = myQuestions[Random.nextInt(myQuestions.size)].name
            } while((candidateOption in options) || (candidateOption == myQuestions[_questionIndex.value!!].name))
            options.add(candidateOption)
        }
        options[Random.nextInt(optionsNum)] = myQuestions[_questionIndex.value!!].name
        return options
    }

    data class MyQuestion(val picUrl: String, val name: String)
    var myQuestions =  mutableListOf<MyQuestion>()

    private val _downLoadCompleted = MutableLiveData(false)
    val downLoadCompleted: LiveData<Boolean> = _downLoadCompleted

    private val _downloadText = MutableLiveData("Download page")
    val downloadText: LiveData<String> = _downloadText

    fun download(
        url: String = "https://it.wikipedia.org/wiki/Lista_di_bandiere_nazionali"
    ) {
        _downloadText.value = "Downloading data..."
        _downLoadCompleted.value = false

        lateinit var doc: Document

        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                // Blocking network request code
                doc = Jsoup.connect(url).get()
                val flags: Elements = doc.select("img")
                for (flag in flags) {
                    myQuestions.add(MyQuestion(
                        picUrl = "https:" + flag.attr("src").toString().replace("/200px", "/800px"),
//                        picUrl = flag.attr("src"),
                        name = flag.attr("alt").toString().replace("Flag of ", "").replace(".svg", "")
                    ))
                }

                myQuestions =
                    myQuestions.dropLast(5) as MutableList<QuizViewModel.MyQuestion> // fake data for last 3 items
                myQuestions.shuffle()
                _downLoadCompleted.postValue(true)
                _downloadText.postValue("Downloading completed!")

                _options.postValue(buildOptions())
            }
        }
    }

}