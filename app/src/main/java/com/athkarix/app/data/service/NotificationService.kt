package com.athkarix.app.data.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.athkarix.app.R
import com.athkarix.app.data.repository.AthkarRepository
import java.util.Calendar


/** BroadcastReceiver that displays athkar reminder notifications when AlarmManager fires. */
class AthkarReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = when (intent.action) {
            "SHOW_MORNING_REMINDER" -> "أذكار الصباح"
            "SHOW_EVENING_REMINDER" -> "أذكار المساء"
            else -> "تذكير بالأذكار"
        }
        val bodyText = when (intent.action) {
            "SHOW_MORNING_REMINDER" -> {
                AthkarRepository.athkarSabahList.random().duaText
                    ?.take(200)
                    ?: "حان وقت قراءة أذكار الصباح"
            }
            "SHOW_EVENING_REMINDER" -> {
                AthkarRepository.athkarMassaList.random().duaText
                    ?.take(200)
                    ?: "حان وقت قراءة أذكار المساء"
            }
            else -> "حان وقت قراءة الأذكار"
        }
        val notification = NotificationCompat.Builder(context, NotificationService.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(bodyText.take(120))
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

/** Manages notification channel creation and AlarmManager-based scheduling for morning/evening reminders. */
class NotificationService(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "athkar_reminders"
        const val MORNING_REQUEST_CODE = 1001
        const val EVENING_REQUEST_CODE = 1002
    }

    // — Channel & scheduling —
    fun initialize() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "أذكار",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "تذكير بأذكار الصباح والمساء"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun scheduleMorning(hour: Int, minute: Int) {
        schedule(hour, minute, "SHOW_MORNING_REMINDER", MORNING_REQUEST_CODE)
    }

    fun scheduleEvening(hour: Int, minute: Int) {
        schedule(hour, minute, "SHOW_EVENING_REMINDER", EVENING_REQUEST_CODE)
    }

    private fun schedule(hour: Int, minute: Int, action: String, requestCode: Int) {
        val intent = Intent(context, AthkarReminderReceiver::class.java).apply { this.action = action }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // — Cancellation —
    fun cancelMorning() = cancel(MORNING_REQUEST_CODE)
    fun cancelEvening() = cancel(EVENING_REQUEST_CODE)
    fun cancelAll() { cancelMorning(); cancelEvening() }

    private fun cancel(requestCode: Int) {
        val intent = Intent(context, AthkarReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
