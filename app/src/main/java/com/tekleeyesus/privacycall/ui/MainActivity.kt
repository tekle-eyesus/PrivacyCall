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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        checkPermissions()
        setupClickListeners()
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
    }
    
    private fun enablePrivacyCall() {
        if (isDefaultDialer()) {
            updateStatus("PrivacyCall Active - Screening unknown calls")
            showFeatureEnabledMessage()
        } else {
            requestDefaultDialer()
        }
    }
    
    private fun disablePrivacyCall() {
        updateStatus("PrivacyCall Inactive")
        showFeatureDisabledMessage()
    }
    
    private fun isDefaultDialer(): Boolean {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        return packageName == telecomManager.defaultDialerPackage
    }
    
    private fun requestDefaultDialer() {
        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
            putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        }
        startActivity(intent)
    }
    
    private fun checkDefaultDialerStatus() {
        if (isDefaultDialer()) {
            binding.txtDefaultDialerStatus.text = "Default dialer: Yes"
            binding.txtDefaultDialerStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else {
            binding.txtDefaultDialerStatus.text = "Default dialer: No (Required)"
            binding.txtDefaultDialerStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }
    
    private fun testCallScreening() {
        // Simulate call screening for testing
        Timber.d("Testing call screening...")
        updateStatus("Testing call screening...")
        
        // Start the interception service to test it's working
        val serviceIntent = Intent(this, CallInterceptionService::class.java).apply {
            putExtra(CallInterceptionService.EXTRA_INCOMING_NUMBER, "+1234567890")
            action = CallInterceptionService.ACTION_START_SCREENING
        }
        startService(serviceIntent)
        
        // Stop after 3 seconds for testing
        binding.root.postDelayed({
            val stopIntent = Intent(this, CallInterceptionService::class.java).apply {
                action = CallInterceptionService.ACTION_STOP_SCREENING
            }
            startService(stopIntent)
            updateStatus("Test completed - Ready")
        }, 3000)
    }
    
    private fun updateStatus(status: String) {
        binding.txtStatus.text = "Status: $status"
    }
    
    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.permission_explanation))
            .setPositiveButton(getString(R.string.grant_permissions)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                finish()
            }
            .show()
    }
    
    private fun showFeatureEnabledMessage() {
        AlertDialog.Builder(this)
            .setTitle("PrivacyCall Enabled")
            .setMessage("PrivacyCall is now active and will screen all incoming calls from unknown numbers.")
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showFeatureDisabledMessage() {
        AlertDialog.Builder(this)
            .setTitle("PrivacyCall Disabled")
            .setMessage("PrivacyCall is now inactive. All calls will proceed normally.")
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun openSettings() {
        // Will implement settings activity later
        Timber.d("Opening settings...")
    }
    
    private fun openCallHistory() {
        // Will implement call history later
        Timber.d("Opening call history...")
    }
    
    override fun onResume() {
        super.onResume()
        checkDefaultDialerStatus()
    }
}