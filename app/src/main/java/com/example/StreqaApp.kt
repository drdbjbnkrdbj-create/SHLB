package com.example

import android.app.Application
import android.content.Context
import com.example.data.local.StreqaDatabase
import com.example.data.repository.HabitRepository
import com.example.notifications.NotificationHelper
import com.example.notifications.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class StreqaApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { StreqaDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { HabitRepository(database.habitDao()) }

    override fun onCreate() {
        super.onCreate()
        // Setup notification channel
        NotificationHelper.createNotificationChannel(this)

        // Setup default daily reminder at 21:00 (9:00 PM) if first run
        val prefs = getSharedPreferences("streqa_prefs", Context.MODE_PRIVATE)
        if (!prefs.contains("reminder_initialized")) {
            prefs.edit()
                .putBoolean("reminder_initialized", true)
                .putBoolean("reminder_enabled", true)
                .putInt("reminder_hour", 21)
                .putInt("reminder_minute", 0)
                .apply()
            ReminderScheduler.scheduleDailyReminder(this, 21, 0)
        }
    }
}
