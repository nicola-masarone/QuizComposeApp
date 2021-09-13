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

    private val _points = MutableLiveData(0)
    val points: LiveData<Int> = _points
    fun updatePoints(newPts: Int) {
        _points.value = newPts
    }

    private val _questionIndex = MutableLiveData(0)
    val questionIndex: LiveData<Int> = _questionIndex
    fun nextQuestion() {
        _questionIndex.value = (_questionIndex.value?.plus(1))?.rem(myQuestions.size)
        _options.value = buildOptions()
    }

    private val _selOption = MutableLiveData("")
    val selOption: LiveData<String> = _selOption
    fun setSelOption(option: String) {
        _selOption.value = option
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

                val altFlags: Elements = doc.select("td:has(a.image)")
                for (altFlag in altFlags) {
                    var childCounter = 0
                    var url: String = ""
                    var name: String = ""
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
                    myQuestions.add(MyQuestion(url, name))
                }

                myQuestions =
                    myQuestions.dropLast(1) as MutableList<QuizViewModel.MyQuestion> // fake data for last 1 item
                myQuestions.shuffle()
                _downLoadCompleted.postValue(true)
                _downloadText.postValue("Downloading completed!")

                _options.postValue(buildOptions())
            }
        }
    }

}