package com.tekleeyesus.privacycall.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tekleeyesus.privacycall.R
import com.tekleeyesus.privacycall.databinding.ActivityMainBinding
import com.tekleeyesus.privacycall.util.PermissionHelper

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
        setupUI()
        // Start services if needed
    }
    
    private fun setupUI() {
        binding.apply {
            togglePrivacyCall.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Enable PrivacyCall features
                    showFeatureEnabledMessage()
                } else {
                    // Disable PrivacyCall features
                    showFeatureDisabledMessage()
                }
            }
            
            btnSettings.setOnClickListener {
                // Navigate to settings
                openSettings()
            }
            
            btnCallHistory.setOnClickListener {
                // Navigate to call history
                openCallHistory()
            }
        }
    }
    
    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("PrivacyCall needs phone and microphone permissions to screen your calls and alter your voice for privacy protection.")
            .setPositiveButton("Grant Permissions") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .show()
    }
    
    private fun showFeatureEnabledMessage() {
        // Show a message that PrivacyCall is now active
    }
    
    private fun showFeatureDisabledMessage() {
        // Show a message that PrivacyCall is now inactive
    }
    
    private fun openSettings() {
        // Will implement later
    }
    
    private fun openCallHistory() {
        // Will implement later
    }
}