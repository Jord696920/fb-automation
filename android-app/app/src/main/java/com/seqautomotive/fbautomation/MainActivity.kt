package com.seqautomotive.fbautomation

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var sessionToggle: SwitchMaterial
    private lateinit var contactedCarsRecyclerView: RecyclerView
    private lateinit var contactedCarsAdapter: ContactedCarsAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupRecyclerView()
        checkPermissions()
        
        // Start app detection service
        startService(Intent(this, AppDetectionService::class.java))
    }
    
    private fun initViews() {
        sessionToggle = findViewById(R.id.session_toggle)
        contactedCarsRecyclerView = findViewById(R.id.contacted_cars_recycler)
        
        sessionToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (hasOverlayPermission() && hasUsageStatsPermission()) {
                    FloatingButtonService.toggleSession(this)
                    Toast.makeText(this, "Session started - floating button will appear on Facebook", 
                        Toast.LENGTH_LONG).show()
                } else {
                    sessionToggle.isChecked = false
                    requestPermissions()
                }
            } else {
                FloatingButtonService.toggleSession(this)
                Toast.makeText(this, "Session stopped", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupRecyclerView() {
        contactedCarsAdapter = ContactedCarsAdapter { car ->
            // Handle delete action
            lifecycleScope.launch {
                FBAutomationApp.instance.database.contactedCarDao().deleteContactedCar(car.id)
            }
        }
        
        contactedCarsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactedCarsAdapter
        }
        
        // Observe contacted cars
        lifecycleScope.launch {
            FBAutomationApp.instance.database.contactedCarDao().getAllContactedCars()
                .collect { cars ->
                    contactedCarsAdapter.submitList(cars)
                }
        }
    }
    
    private fun checkPermissions() {
        if (!hasOverlayPermission()) {
            requestOverlayPermission()
        }
        
        if (!hasUsageStatsPermission()) {
            requestUsageStatsPermission()
        }
    }
    
    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }
    
    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    private fun requestPermissions() {
        if (!hasOverlayPermission()) {
            requestOverlayPermission()
        } else if (!hasUsageStatsPermission()) {
            requestUsageStatsPermission()
        }
    }
    
    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
    }
    
    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivityForResult(intent, REQUEST_USAGE_STATS_PERMISSION)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            REQUEST_OVERLAY_PERMISSION -> {
                if (hasOverlayPermission()) {
                    if (!hasUsageStatsPermission()) {
                        requestUsageStatsPermission()
                    }
                } else {
                    Toast.makeText(this, "Overlay permission is required", Toast.LENGTH_LONG).show()
                }
            }
            REQUEST_USAGE_STATS_PERMISSION -> {
                if (!hasUsageStatsPermission()) {
                    Toast.makeText(this, "Usage access permission is required", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 1001
        private const val REQUEST_USAGE_STATS_PERMISSION = 1002
    }
}