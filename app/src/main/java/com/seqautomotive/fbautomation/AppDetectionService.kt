package com.seqautomotive.fbautomation

import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*

class AppDetectionService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isMonitoring = false
    
    companion object {
        private const val FACEBOOK_PACKAGE = "com.facebook.katana"
        private const val FACEBOOK_LITE_PACKAGE = "com.facebook.lite"
        private const val CHECK_INTERVAL = 1000L // Check every second
        
        var isFacebookActive = false
            private set
            
        var onFacebookStateChanged: ((Boolean) -> Unit)? = null
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMonitoring()
        return START_STICKY
    }
    
    private fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
        
        serviceScope.launch {
            while (isMonitoring) {
                val currentApp = getCurrentForegroundApp()
                val newFacebookState = isFacebookApp(currentApp)
                
                if (newFacebookState != isFacebookActive) {
                    isFacebookActive = newFacebookState
                    onFacebookStateChanged?.invoke(isFacebookActive)
                }
                
                delay(CHECK_INTERVAL)
            }
        }
    }
    
    private fun getCurrentForegroundApp(): String? {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - 1000 * 60, // Last minute
            time
        )
        
        return usageStats?.maxByOrNull { it.lastTimeUsed }?.packageName
    }
    
    private fun isFacebookApp(packageName: String?): Boolean {
        return packageName == FACEBOOK_PACKAGE || packageName == FACEBOOK_LITE_PACKAGE
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isMonitoring = false
        serviceScope.cancel()
    }
}