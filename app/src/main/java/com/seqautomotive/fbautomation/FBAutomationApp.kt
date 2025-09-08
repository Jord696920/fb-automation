package com.seqautomotive.fbautomation

import android.app.Application
import androidx.room.Room

class FBAutomationApp : Application() {
    
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            ContactedCarDatabase::class.java,
            "contacted_cars_database"
        ).build()
    }
    
    companion object {
        lateinit var instance: FBAutomationApp
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}