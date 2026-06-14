package com.athkarix.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FloatingCounterViewModel : ViewModel() {

    private val _counters = MutableStateFlow<Map<String, Int>>(emptyMap())
    val counters: StateFlow<Map<String, Int>> = _counters.asStateFlow()

    fun increment(screenKey: String) {
        _counters.value = _counters.value.toMutableMap().apply {
            put(screenKey, (get(screenKey) ?: 0) + 1)
        }
    }

    fun reset(screenKey: String) {
        _counters.value = _counters.value.toMutableMap().apply {
            put(screenKey, 0)
        }
    }
}
