package com.athkarix.app

import android.app.Application

/** Application entry point — exposes the singleton instance. */
class AthkarixApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AthkarixApp
            private set
    }
}
