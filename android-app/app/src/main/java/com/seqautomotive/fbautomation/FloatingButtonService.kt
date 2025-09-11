
package com.seqautomotive.fbautomation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import android.content.pm.ServiceInfo
import kotlinx.coroutines.*

class FloatingButtonService : Service() {
    
    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val ocrProcessor = OCRProcessor()
    
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "floating_button_channel"
        
        var isSessionActive = false
            private set
            
        fun toggleSession(context: Context) {
            isSessionActive = !isSessionActive
            val intent = Intent(context, FloatingButtonService::class.java)
            intent.action = if (isSessionActive) "START_SESSION" else "STOP_SESSION"
            context.startForegroundService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        
        // Listen for Facebook app state changes
        AppDetectionService.onFacebookStateChanged = { isFacebookActive ->
            if (isSessionActive) {
                if (isFacebookActive) {
                    showFloatingButton()
                } else {
                    hideFloatingButton()
                }
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_SESSION" -> {
                isSessionActive = true
                val notif = NotificationCompat.Builder(this, "fb_session")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("FB Automation running")
                    .setContentText("Floating button active on Facebook")
                    .setOngoing(true)
                    .build()
                val id = 1001
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(id, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
                } else {
                    startForeground(id, notif)
                }
                try {
                    if (AppDetectionService.isFacebookActive) showFloatingButton()
                } catch (_: Throwable) {}
            }
            "STOP_SESSION" -> {
                isSessionActive = false
                hideFloatingButton()
                stopForeground(true)
                stopSelf()
            }
            "SHOW" -> showFloatingButton()
            "HIDE" -> hideFloatingButton()
        }
        return START_STICKY
    }
    
    private fun showFloatingButton() {
        if (floatingView != null) return
        
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_button, null)
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 50
        params.y = 200
        
        floatingView?.findViewById<ImageView>(R.id.floating_button)?.setOnClickListener {
            processScreenshot()
        }
        
        windowManager.addView(floatingView, params)
    }
    
    private fun hideFloatingButton() {
        floatingView?.let {
            windowManager.removeView(it)
            floatingView = null
        }
    }
    
    private fun processScreenshot() {
        serviceScope.launch {
            try {
                // Take screenshot (this would require media projection API)
                // For now, we'll simulate with a placeholder
                val bitmap = takeScreenshot()
                
                if (bitmap != null) {
                    val (carModel, sellerName) = ocrProcessor.extractCarAndSellerInfo(bitmap)
                    
                    val message = MessageTemplate.generateMessage(sellerName, carModel)
                    copyToClipboard(message)
                    
                    // Save to database
                    if (carModel != null && sellerName != null) {
                        val contactedCar = ContactedCar(
                            carModel = carModel,
                            sellerName = sellerName
                        )
                        FBAutomationApp.instance.database.contactedCarDao().insertContactedCar(contactedCar)
                    }
                    
                    Toast.makeText(this@FloatingButtonService, 
                        "Message copied to clipboard!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@FloatingButtonService, 
                        "Failed to capture screen", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@FloatingButtonService, 
                    "Error processing screenshot", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun takeScreenshot(): Bitmap? {
        // This is a placeholder - actual implementation would require MediaProjection API
        // which needs user permission and is more complex
        return null
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("FB Automation Message", text)
        clipboard.setPrimaryClip(clip)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Button Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FB Automation Active")
            .setContentText("Session is running - floating button will appear on Facebook")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        hideFloatingButton()
        serviceScope.cancel()
    }
}
