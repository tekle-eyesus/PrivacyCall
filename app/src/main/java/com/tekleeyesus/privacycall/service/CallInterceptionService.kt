package com.tekleeyesus.privacycall.service

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tekleeyesus.privacycall.R
import com.tekleeyesus.privacycall.ui.MainActivity
import com.tekleeyesus.privacycall.util.NotificationHelper
import timber.log.Timber

class CallInterceptionService : Service() {

    private val binder = CallInterceptionBinder()
    private var isScreeningActive = false
    private var currentIncomingNumber: String? = null

    companion object {
        const val EXTRA_INCOMING_NUMBER = "incoming_number"
        const val ACTION_START_SCREENING = "start_screening"
        const val ACTION_STOP_SCREENING = "stop_screening"
        const val ONGOING_NOTIFICATION_ID = 1001
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("CallInterceptionService started with action: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_START_SCREENING -> {
                val number = intent.getStringExtra(EXTRA_INCOMING_NUMBER)
                startCallScreening(number)
            }
            ACTION_STOP_SCREENING -> {
                stopCallScreening()
            }
        }
        
        return START_STICKY
    }

    private fun startCallScreening(phoneNumber: String?) {
        if (phoneNumber.isNullOrEmpty()) {
            Timber.w("No phone number provided for screening")
            return
        }

        currentIncomingNumber = phoneNumber
        isScreeningActive = true
        
        // Start as foreground service to avoid being killed by the system
        startForegroundService()
        
        Timber.d("Started call screening for: $phoneNumber")
        
        // Here we would:
        // 1. Programmatically answer the call (requires AccessibilityService workaround)
        // 2. Play pre-recorded message
        // 3. Start speech-to-text for caller response
        // 4. Process with AI chatbot
        
        // For now, just show a notification
        showScreeningNotification(phoneNumber)
    }

    private fun stopCallScreening() {
        Timber.d("Stopping call screening")
        isScreeningActive = false
        currentIncomingNumber = null
        
        // Stop foreground service and remove notification
        stopForeground(true)
        stopSelf()
    }

    private fun startForegroundService() {
        val notification = createOngoingNotification()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createOngoingNotification(): Notification {
        val notificationHelper = NotificationHelper(this)
        
        // Create intent for when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return notificationHelper.createCallScreeningNotification()
            .setContentTitle("Screening Call")
            .setContentText("Processing call from: $currentIncomingNumber")
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun showScreeningNotification(phoneNumber: String) {
        // This would be a different notification for urgent calls
        // We'll implement this later with the chatbot integration
    }

    inner class CallInterceptionBinder : Binder() {
        fun getService(): CallInterceptionService = this@CallInterceptionService
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("CallInterceptionService destroyed")
    }
}