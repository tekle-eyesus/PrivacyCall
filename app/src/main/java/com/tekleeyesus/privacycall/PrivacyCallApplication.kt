package com.tekleeyesus.privacycall

import android.app.Application
import com.tekleeyesus.privacycall.util.NotificationHelper
import timber.log.Timber

class PrivacyCallApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize logging
        Timber.plant(Timber.DebugTree())

        // Initialize notification channels
        NotificationHelper.createNotificationChannels(this)

        Timber.d("PrivacyCall Application started")
    }
}