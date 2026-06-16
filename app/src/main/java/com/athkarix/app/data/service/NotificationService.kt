package com.athkarix.app.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationService(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "athkar_reminders"
        private const val MORNING_WORK_NAME = "morning_reminder"
        private const val EVENING_WORK_NAME = "evening_reminder"
    }

    fun initialize() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(com.athkarix.app.R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(com.athkarix.app.R.string.notification_channel_desc)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun scheduleMorning(hour: Int, minute: Int) {
        schedule(MORNING_WORK_NAME, hour, minute, "morning")
    }

    fun scheduleEvening(hour: Int, minute: Int) {
        schedule(EVENING_WORK_NAME, hour, minute, "evening")
    }

    private fun schedule(workName: String, hour: Int, minute: Int, type: String) {
        val delay = calculateInitialDelay(hour, minute)
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("type" to type))
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(workName, ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    fun cancelMorning() = cancel(MORNING_WORK_NAME)
    fun cancelEvening() = cancel(EVENING_WORK_NAME)
    fun cancelAll() { cancelMorning(); cancelEvening() }

    private fun cancel(workName: String) {
        WorkManager.getInstance(context).cancelUniqueWork(workName)
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}
