package com.athkarix.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FloatingCounterViewModel : ViewModel() {

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    fun increment() {
        _counter.value++
    }

    fun incrementUntil100() {
        val next = _counter.value + 1
        _counter.value = if (next >= 100) 0 else next
    }

    fun reset() {
        _counter.value = 0
    }
}
