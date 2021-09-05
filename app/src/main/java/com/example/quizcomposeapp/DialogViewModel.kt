package com.example.quizcomposeapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DialogViewModel: ViewModel() {

    private val _visibility = MutableLiveData(false)
    val visibility : LiveData<Boolean> = _visibility

    private val _text = MutableLiveData("")
    val text : LiveData<String> = _text

    private val _icon = MutableLiveData(Icons.Filled.ThumbUp)
    val icon: LiveData<ImageVector> = _icon

    fun setVisibility(visibility: Boolean) {
        _visibility.value = visibility
    }

    fun setText(text: String) {
        _text.value = text
    }

    fun setIcon(icon: ImageVector) {
        _icon.value = icon
    }

}