package com.tekleeyesus.privacycall.service

import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import timber.log.Timber

class PrivacyCallConnectionService : ConnectionService() {

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest
    ): Connection {
        Timber.d("Creating outgoing connection")
        return PrivacyCallConnection().apply {
            setConnectionProperties(Connection.PROPERTY_SELF_MANAGED)
        }
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest
    ): Connection {
        Timber.d("Creating incoming connection")
        return PrivacyCallConnection().apply {
            setConnectionProperties(Connection.PROPERTY_SELF_MANAGED)
            setRinging()
        }
    }

    private inner class PrivacyCallConnection : Connection() {
        init {
            Timber.d("PrivacyCallConnection created")
        }

        override fun onAnswer() {
            Timber.d("Call answered")
            super.onAnswer()
            setActive()
        }

        override fun onReject() {
            Timber.d("Call rejected")
            super.onReject()
            setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
            destroy()
        }

        override fun onDisconnect() {
            Timber.d("Call disconnected")
            super.onDisconnect()
            setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
            destroy()
        }

        override fun onAbort() {
            Timber.d("Call aborted")
            super.onAbort()
            setDisconnected(DisconnectCause(DisconnectCause.CANCELED))
            destroy()
        }
    }
}