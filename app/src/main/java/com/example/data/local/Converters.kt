package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.HabitStatus

class Converters {
    @TypeConverter
    fun fromStatus(status: HabitStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): HabitStatus {
        return try {
            HabitStatus.valueOf(value)
        } catch (e: Exception) {
            HabitStatus.NONE
        }
    }
}
