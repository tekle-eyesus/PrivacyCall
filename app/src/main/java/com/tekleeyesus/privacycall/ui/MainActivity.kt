package com.tekleeyesus.privacycall.ui

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tekleeyesus.privacycall.R
import com.tekleeyesus.privacycall.databinding.ActivityMainBinding
import com.tekleeyesus.privacycall.service.CallInterceptionService
import com.tekleeyesus.privacycall.util.PermissionHelper
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val permissionHelper = PermissionHelper(this)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            showPermissionExplanationDialog()
        } else {
            initializeApp()
        }
    }

    // Result launcher for default dialer request
    private val defaultDialerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Check if user set us as default dialer
        checkDefaultDialerStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        checkPermissions()
    }

    private fun setupClickListeners() {
        binding.apply {
            togglePrivacyCall.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    enablePrivacyCall()
                } else {
                    disablePrivacyCall()
                }
            }

            btnSettings.setOnClickListener {
                openSettings()
            }

            btnCallHistory.setOnClickListener {
                openCallHistory()
            }

            btnTestCallScreening.setOnClickListener {
                testCallScreening()
            }
        }
    }

    private fun checkPermissions() {
        val requiredPermissions = permissionHelper.getRequiredPermissions()
        if (permissionHelper.hasAllPermissions(requiredPermissions)) {
            initializeApp()
        } else {
            permissionLauncher.launch(requiredPermissions)
        }
    }

    private fun initializeApp() {
        updateStatus("Ready - Monitoring calls")
        checkDefaultDialerStatus()

        // Set initial toggle state based on default dialer status
        binding.togglePrivacyCall.isChecked = isDefaultDialer()
    }

    private fun enablePrivacyCall() {
        Timber.d("Enabling PrivacyCall")

        if (isDefaultDialer()) {
            updateStatus("PrivacyCall Active - Screening unknown calls")
            showFeatureEnabledMessage()
            // Start any background services if needed
        } else {
            updateStatus("Please set as default dialer")
            requestDefaultDialer()
            // Temporarily uncheck until user sets as default dialer
            binding.togglePrivacyCall.isChecked = false
        }
    }

    private fun disablePrivacyCall() {
        Timber.d("Disabling PrivacyCall")
        updateStatus("PrivacyCall Inactive")
        showFeatureDisabledMessage()
        // Stop any background services if needed
    }

    private fun isDefaultDialer(): Boolean {
        return try {
            val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
            val defaultDialer = telecomManager.defaultDialerPackage
            Timber.d("Default dialer: $defaultDialer, Our package: $packageName")
            packageName == defaultDialer
        } catch (e: Exception) {
            Timber.e(e, "Error checking default dialer")
            false
        }
    }

    private fun requestDefaultDialer() {
    Timber.d("Requesting default dialer")
    
    try {
        // Method 1: Standard telecom intent
        val telecomIntent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
            putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        }
        
        // Method 2: Direct settings intent
        val settingsIntent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        
        // Try telecom intent first, fallback to settings
        if (telecomIntent.resolveActivity(packageManager) != null) {
            Timber.d("Using telecom intent for default dialer")
            defaultDialerLauncher.launch(telecomIntent)
        } else if (settingsIntent.resolveActivity(packageManager) != null) {
            Timber.d("Using settings intent for default dialer")
            defaultDialerLauncher.launch(settingsIntent)
        } else {
            // Final fallback
            Toast.makeText(this, 
                "Please go to Settings → Apps → Default apps → Phone app and select 'PrivacyCall'", 
                Toast.LENGTH_LONG
            ).show()
            openAppSettings()
        }
    } catch (e: Exception) {
        Timber.e(e, "Error requesting default dialer")
        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

    private fun checkDefaultDialerStatus() {
        val isDefault = isDefaultDialer()
        Timber.d("Default dialer check: $isDefault")

        if (isDefault) {
            binding.txtDefaultDialerStatus.text = "Default dialer: Yes"
            binding.txtDefaultDialerStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
            // Auto-enable PrivacyCall if user just set it as default
            binding.togglePrivacyCall.isChecked = true
            enablePrivacyCall()
        } else {
            binding.txtDefaultDialerStatus.text = "Default dialer: No (Required)"
            binding.txtDefaultDialerStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.togglePrivacyCall.isChecked = false
        }
    }

    private fun testCallScreening() {
        Timber.d("Testing call screening...")
        updateStatus("Testing call screening...")

        // Show a test notification
        val notificationHelper = com.tekleeyesus.privacycall.util.NotificationHelper(this)
        val notification = notificationHelper.createCallScreeningNotification()
            .setContentTitle("Test Screening")
            .setContentText("Testing call screening functionality")
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(999, notification)

        // Simulate service start
        val serviceIntent = Intent(this, CallInterceptionService::class.java).apply {
            putExtra(CallInterceptionService.EXTRA_INCOMING_NUMBER, "+1234567890")
            action = CallInterceptionService.ACTION_START_SCREENING
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // Stop after 3 seconds for testing
        binding.root.postDelayed({
            val stopIntent = Intent(this, CallInterceptionService::class.java).apply {
                action = CallInterceptionService.ACTION_STOP_SCREENING
            }
            startService(stopIntent)
            updateStatus("Test completed - Ready")
            notificationManager.cancel(999)
        }, 3000)
    }

    private fun updateStatus(status: String) {
        binding.txtStatus.text = "Status: $status"
        Timber.d("Status updated: $status")
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.permission_explanation))
            .setPositiveButton(getString(R.string.grant_permissions)) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                finish()
            }
            .show()
    }

    private fun showFeatureEnabledMessage() {
        Toast.makeText(this, "PrivacyCall Enabled - Screening unknown calls", Toast.LENGTH_SHORT).show()
    }

    private fun showFeatureDisabledMessage() {
        Toast.makeText(this, "PrivacyCall Disabled", Toast.LENGTH_SHORT).show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun openSettings() {
        Timber.d("Opening settings...")
        Toast.makeText(this, "Settings will be implemented soon", Toast.LENGTH_SHORT).show()
    }

    private fun openCallHistory() {
        Timber.d("Opening call history...")
        Toast.makeText(this, "Call history will be implemented soon", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Check default dialer status every time app comes to foreground
        checkDefaultDialerStatus()
    }
}