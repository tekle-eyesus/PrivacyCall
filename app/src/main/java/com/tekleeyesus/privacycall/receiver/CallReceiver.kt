package com.tekleeyesus.privacycall.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.tekleeyesus.privacycall.service.CallInterceptionService
import com.tekleeyesus.privacycall.util.ContactHelper
import timber.log.Timber

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            Timber.d("Call state: $state, Number: $incomingNumber")

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    if (!incomingNumber.isNullOrEmpty()) {
                        handleIncomingCall(context, incomingNumber)
                    }
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Timber.d("Call answered/started")
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Timber.d("Call ended")
                    stopCallInterceptionService(context)
                }
            }
        }
    }

    private fun handleIncomingCall(context: Context, phoneNumber: String) {
        val contactHelper = ContactHelper(context)
        val isKnownContact = contactHelper.isNumberInContacts(phoneNumber)

        Timber.d("Number $phoneNumber is known contact: $isKnownContact")

        if (!isKnownContact) {
            startCallInterceptionService(context, phoneNumber)
        }
    }

    private fun startCallInterceptionService(context: Context, phoneNumber: String) {
        val serviceIntent = Intent(context, CallInterceptionService::class.java).apply {
            putExtra(CallInterceptionService.EXTRA_INCOMING_NUMBER, phoneNumber)
            action = CallInterceptionService.ACTION_START_SCREENING
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    private fun stopCallInterceptionService(context: Context) {
        val serviceIntent = Intent(context, CallInterceptionService::class.java).apply {
            action = CallInterceptionService.ACTION_STOP_SCREENING
        }
        context.startService(serviceIntent)
    }
}