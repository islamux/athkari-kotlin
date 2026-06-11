package com.athkarix.app

import android.app.Application
import com.athkarix.app.data.service.NotificationService

/** Application entry point — initialises notification channel and exposes the singleton instance. */
class AthkarixApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        NotificationService(this).initialize()
      
    }

    companion object {
        lateinit var instance: AthkarixApp
            private set
    }
}
