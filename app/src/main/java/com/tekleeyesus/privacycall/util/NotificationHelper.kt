package com.tekleeyesus.privacycall.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    companion object {
        const val CALL_SCREENING_CHANNEL_ID = "call_screening_channel"
        const val URGENT_CALL_CHANNEL_ID = "urgent_call_channel"
        const val ONGOING_CALL_CHANNEL_ID = "ongoing_call_channel"

        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val screeningChannel = NotificationChannel(
                    CALL_SCREENING_CHANNEL_ID,
                    "Call Screening",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for call screening activities"
                }

                val urgentChannel = NotificationChannel(
                    URGENT_CALL_CHANNEL_ID,
                    "Urgent Calls",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for urgent screened calls"
                }

                val ongoingChannel = NotificationChannel(
                    ONGOING_CALL_CHANNEL_ID,
                    "Ongoing Calls",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Ongoing call notifications"
                }

                notificationManager.createNotificationChannels(
                    listOf(screeningChannel, urgentChannel, ongoingChannel)
                )
            }
        }
    }

    fun createCallScreeningNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CALL_SCREENING_CHANNEL_ID)
            .setContentTitle("PrivacyCall Active")
            .setContentText("Screening incoming calls")
            .setSmallIcon(android.R.drawable.ic_lock_lock) // System lock icon
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
    }
}