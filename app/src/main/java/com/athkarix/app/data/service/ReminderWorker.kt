package com.athkarix.app.data.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.athkarix.app.R
import com.athkarix.app.data.repository.AthkarRepository
import java.util.concurrent.atomic.AtomicInteger

class ReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val type = inputData.getString("type") ?: return Result.failure()
        val (title, bodyText) = when (type) {
            "morning" -> {
                val text = AthkarRepository.athkarSabahList.random().duaText?.take(200)
                    ?: applicationContext.getString(com.athkarix.app.R.string.notification_morning_body_default)
                applicationContext.getString(com.athkarix.app.R.string.notification_morning_title) to text
            }
            "evening" -> {
                val text = AthkarRepository.athkarMassaList.random().duaText?.take(200)
                    ?: applicationContext.getString(com.athkarix.app.R.string.notification_evening_body_default)
                applicationContext.getString(com.athkarix.app.R.string.notification_evening_title) to text
            }
            else -> return Result.failure()
        }

        val notification = NotificationCompat.Builder(applicationContext, NotificationService.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(bodyText.take(120))
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationIdCounter.incrementAndGet(), notification)
        return Result.success()
    }

    companion object {
        private val notificationIdCounter = AtomicInteger(0)
    }
}
