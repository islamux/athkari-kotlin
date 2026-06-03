package com.athkarix.app.data.local

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var morningEnabled: Boolean
        get() = prefs.getBoolean(KEY_MORNING_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_MORNING_ENABLED, value).apply()

    var eveningEnabled: Boolean
        get() = prefs.getBoolean(KEY_EVENING_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_EVENING_ENABLED, value).apply()

    var morningHour: Int
        get() = prefs.getInt(KEY_MORNING_HOUR, 8)
        set(value) = prefs.edit().putInt(KEY_MORNING_HOUR, value).apply()

    var morningMinute: Int
        get() = prefs.getInt(KEY_MORNING_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_MORNING_MINUTE, value).apply()

    var eveningHour: Int
        get() = prefs.getInt(KEY_EVENING_HOUR, 17)
        set(value) = prefs.edit().putInt(KEY_EVENING_HOUR, value).apply()

    var eveningMinute: Int
        get() = prefs.getInt(KEY_EVENING_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_EVENING_MINUTE, value).apply()

    companion object {
        private const val PREFS_NAME = "athkarix_prefs"
        private const val KEY_MORNING_ENABLED = "notify_morning_enabled"
        private const val KEY_EVENING_ENABLED = "notify_evening_enabled"
        private const val KEY_MORNING_HOUR = "notify_morning_hour"
        private const val KEY_MORNING_MINUTE = "notify_morning_minute"
        private const val KEY_EVENING_HOUR = "notify_evening_hour"
        private const val KEY_EVENING_MINUTE = "notify_evening_minute"
    }
}
