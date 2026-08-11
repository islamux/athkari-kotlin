package com.athkarix.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FontViewModel : ViewModel() {

    private val _fontSize = MutableStateFlow(32.0f)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private val _selectedFont = MutableStateFlow("Amiri")
    val selectedFont: StateFlow<String> = _selectedFont.asStateFlow()

    private val maxFontSize = 42.0f
    private val minFontSize = 24.0f

    fun setFont(font: String) {
        _selectedFont.value = font
    }

    fun toggleFont() {
        _selectedFont.value = if (_selectedFont.value == "Amiri") "NotoNaskh" else "Amiri"
    }

        fun increaseFontSize() {
            if (_fontSize.value < maxFontSize) {
                val newSize = _fontSize.value + 2.0f
                _fontSize.value = if (newSize > maxFontSize) maxFontSize else newSize
            }
        }

        fun decreaseFontSize() {
            if (_fontSize.value > minFontSize) {
                val newSize = _fontSize.value - 2.0f
                _fontSize.value = if (newSize < minFontSize) minFontSize else newSize
            }
        }
    }
