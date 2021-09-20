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

    private val _downloadText = MutableLiveData("Pagina di download")
    val downloadText: LiveData<String> = _downloadText

    enum class QuizSubject {
        FLAGS, DOGS, NONE
    }
    private val _selectedSubject = MutableLiveData(QuizSubject.NONE)
    val selectedSubject : LiveData<QuizSubject> = _selectedSubject
    fun setSelectedSubject (subject: QuizSubject) {
        if (subject!=_selectedSubject.value) {
            _selectedSubject.value = subject
            download()
        }
    }

    fun download() {
        _downloadText.value = "Download dei dati..."
        _downLoadCompleted.value = false

        myQuestions.clear()

        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                // Blocking network request code
                myQuestions = dataDownload(selectedSubject.value!!, myQuestions)
                _downLoadCompleted.postValue(true)
                _downloadText.postValue("Download completato!")
                _options.postValue(buildOptions())
            }
        }
    }

}