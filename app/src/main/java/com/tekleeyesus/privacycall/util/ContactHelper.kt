package com.tekleeyesus.privacycall.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import timber.log.Timber

class ContactHelper(private val context: Context) {
    
    fun isNumberInContacts(phoneNumber: String): Boolean {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                cursor.moveToFirst() // Number exists in contacts if cursor has data
            } ?: false
        } catch (e: SecurityException) {
            Timber.e(e, "Permission denied for contacts access")
            false
        } catch (e: Exception) {
            Timber.e(e, "Error checking contacts")
            false
        }
    }
    
    fun getContactName(phoneNumber: String): String? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting contact name")
            null
        }
    }
}