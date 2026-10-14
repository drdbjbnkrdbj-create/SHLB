package com.example.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.data.model.DaySummary
import com.example.data.model.Habit

@Immutable
data class HabitCardUiModel(
    val habit: Habit,
    val themeColor: Color,
    val daySummaries: List<DaySummary>,
    val streakDays: Int,
    val completionRate: Float,
    val isTodayDone: Boolean,
    val isTodayScheduled: Boolean,
    val todaySummary: DaySummary?
)
