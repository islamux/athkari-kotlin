package com.athkarix.app.viewmodel

import androidx.lifecycle.ViewModel
import com.athkarix.app.data.local.SharedPrefsManager
import com.athkarix.app.data.service.NotificationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationSettingsViewModel(
    private val prefs: SharedPrefsManager,
    private val notificationService: NotificationService,
) : ViewModel() {

    private val _morningEnabled = MutableStateFlow(prefs.morningEnabled)
    val morningEnabled: StateFlow<Boolean> = _morningEnabled.asStateFlow()

    private val _eveningEnabled = MutableStateFlow(prefs.eveningEnabled)
    val eveningEnabled: StateFlow<Boolean> = _eveningEnabled.asStateFlow()

    private val _morningHour = MutableStateFlow(prefs.morningHour)
    val morningHour: StateFlow<Int> = _morningHour.asStateFlow()

    private val _morningMinute = MutableStateFlow(prefs.morningMinute)
    val morningMinute: StateFlow<Int> = _morningMinute.asStateFlow()

    private val _eveningHour = MutableStateFlow(prefs.eveningHour)
    val eveningHour: StateFlow<Int> = _eveningHour.asStateFlow()

    private val _eveningMinute = MutableStateFlow(prefs.eveningMinute)
    val eveningMinute: StateFlow<Int> = _eveningMinute.asStateFlow()

    fun setMorningEnabled(enabled: Boolean) {
        _morningEnabled.value = enabled
        prefs.morningEnabled = enabled
        if (enabled) {
            notificationService.scheduleMorning(_morningHour.value, _morningMinute.value)
        } else {
            notificationService.cancelMorning()
        }
    }

    fun setEveningEnabled(enabled: Boolean) {
        _eveningEnabled.value = enabled
        prefs.eveningEnabled = enabled
        if (enabled) {
            notificationService.scheduleEvening(_eveningHour.value, _eveningMinute.value)
        } else {
            notificationService.cancelEvening()
        }
    }

    fun setMorningTime(hour: Int, minute: Int) {
        _morningHour.value = hour
        _morningMinute.value = minute
        prefs.morningHour = hour
        prefs.morningMinute = minute
        if (_morningEnabled.value) {
            notificationService.cancelMorning()
            notificationService.scheduleMorning(hour, minute)
        }
    }

    fun setEveningTime(hour: Int, minute: Int) {
        _eveningHour.value = hour
        _eveningMinute.value = minute
        prefs.eveningHour = hour
        prefs.eveningMinute = minute
        if (_eveningEnabled.value) {
            notificationService.cancelEvening()
            notificationService.scheduleEvening(hour, minute)
        }
    }
}
