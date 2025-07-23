package com.example.recipttracker

import android.app.Application
import com.example.recipttracker.util.SessionManager // ← Add this
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReceiptTrackerApp: Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}
