package com.example.data.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class HabitStatus {
    NONE,    // Not done yet / pending
    DONE,    // Checked ✓
    FAILED   // Missed / Failed ✗
}

@Immutable
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val iconEmoji: String = "✨",
    val colorHex: String = "#2DD4BF",
    val startDate: String, // YYYY-MM-DD
    val endDate: String,   // YYYY-MM-DD
    val frequencyLabel: String = "يومي", // e.g. "يومي", "الاثنين · الأربعاء · الجمعة", "مرة واحدة في الأسبوع"
    val scheduledDays: String = "1,2,3,4,5,6,7", // 1=Sunday, 7=Saturday
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Immutable
@Entity(
    tableName = "habit_logs",
    indices = [
        Index(value = ["habitId", "date"], unique = true)
    ]
)
data class HabitLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String, // YYYY-MM-DD
    val status: HabitStatus = HabitStatus.NONE,
    val notes: String = "", // سبب النجاح أو الإخفاق
    val updatedAt: Long = System.currentTimeMillis()
)

@Immutable
data class DaySummary(
    val dateStr: String, // YYYY-MM-DD
    val dayOfWeekNameArabic: String, // الأحد، الاثنين، ...
    val dayOfMonth: Int,
    val isToday: Boolean,
    val isFuture: Boolean,
    val isWithinRange: Boolean,
    val isScheduledDay: Boolean,
    val status: HabitStatus,
    val note: String
)
