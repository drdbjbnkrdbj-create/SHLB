package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HabitReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule daily reminder after device reboot
            val prefs = context.getSharedPreferences("streqa_prefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("reminder_enabled", true)
            if (isEnabled) {
                val hour = prefs.getInt("reminder_hour", 21)
                val minute = prefs.getInt("reminder_minute", 0)
                ReminderScheduler.scheduleDailyReminder(context, hour, minute)
            }
        } else {
            // Trigger the daily reminder notification
            val prefs = context.getSharedPreferences("streqa_prefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("reminder_enabled", true)
            if (isEnabled) {
                NotificationHelper.showReminderNotification(
                    context = context,
                    title = "حان وقت تسجيل عاداتك! 🎯",
                    message = "يوم جديد وإنجاز جديد، لا تنسَ توثيق عاداتك وملاحظاتك لليوم."
                )
                // Schedule the next day's alarm
                val hour = prefs.getInt("reminder_hour", 21)
                val minute = prefs.getInt("reminder_minute", 0)
                ReminderScheduler.scheduleDailyReminder(context, hour, minute)
            }
        }
    }
}
