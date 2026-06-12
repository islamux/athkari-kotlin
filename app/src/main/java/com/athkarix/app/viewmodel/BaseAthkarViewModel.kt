package com.athkarix.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.athkarix.app.data.model.AthkarItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Events that any Athkar screen can emit — navigation, completion snackbar, or back. */
sealed class ViewEvent {
    data class NavigateTo(val route: String) : ViewEvent()
    data class ShowCompletion(val message: String) : ViewEvent()
    object NavigateBack : ViewEvent()
}

/** Shared logic for every athkar category ViewModel: page tracking, counter, haptics, completion detection. */
abstract class BaseAthkarViewModel : ViewModel() {

    // — Subclass contracts —
    abstract val dataList: List<AthkarItem>
    abstract val completionMessage: String

    // — Mutable state (private backing, public read-only flow) —
    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex: StateFlow<Int> = _currentPageIndex.asStateFlow()

    private val _currentPageCounter = MutableStateFlow(0)
    val currentPageCounter: StateFlow<Int> = _currentPageCounter.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ViewEvent>()
    val eventFlow: SharedFlow<ViewEvent> = _eventFlow.asSharedFlow()

    // — Haptic feedback so the user feels page turns —
    private val _hapticTrigger = MutableSharedFlow<Unit>()
    val hapticTrigger: SharedFlow<Unit> = _hapticTrigger.asSharedFlow()

    fun resetCounter() {
        _currentPageCounter.value = 0
    }

    fun onPageChanged(index: Int) {
        _currentPageIndex.value = index
        resetCounter()
    }

    // — Core counter logic (advance page when count reaches max, or show completion) —
    fun incrementPageController() {
        val max = 1
        val newCount = _currentPageCounter.value + 1
        if (newCount >= max) {
            _currentPageCounter.value = 0
            val nextIndex = _currentPageIndex.value + 1
            if (nextIndex < dataList.size) {
                _currentPageIndex.value = nextIndex
                viewModelScope.launch { _hapticTrigger.emit(Unit) }
            } else {
                viewModelScope.launch {
                    _eventFlow.emit(ViewEvent.ShowCompletion(completionMessage))
                }
            }
        } else {
            _currentPageCounter.value = newCount
        }
    }

    fun goToPage(index: Int) {
        _currentPageIndex.value = index
        _currentPageCounter.value = 0
    }

    fun goToHome() {
        viewModelScope.launch {
            _eventFlow.emit(ViewEvent.NavigateTo("home"))
        }
    }

    fun getShareText(index: Int): String {
        return dataList.getOrNull(index)?.duaText ?: ""
    }
}
