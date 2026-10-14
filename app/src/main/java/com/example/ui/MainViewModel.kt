package com.example.ui

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.StreqaApp
import com.example.data.model.DaySummary
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HabitStatus
import com.example.notifications.NotificationHelper
import com.example.notifications.ReminderScheduler
import com.example.ui.model.HabitCardUiModel
import com.example.ui.theme.StreqaTeal
import com.example.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class ViewMode {
    GRID,   // شبكة
    WEEK,   // أسبوع
    MONTH   // شهر
}

sealed class DialogState {
    object None : DialogState()
    object AddHabit : DialogState()
    data class EditHabit(val habit: Habit) : DialogState()
    data class DayNote(val habit: Habit, val daySummary: DaySummary) : DialogState()
    data class DeleteConfirm(val habit: Habit) : DialogState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as StreqaApp
    private val repository = app.repository
    private val prefs = application.getSharedPreferences("streqa_prefs", Context.MODE_PRIVATE)

    // UI Navigation & View filters
    private val _currentTab = MutableStateFlow(0) // 0: اليوم, 1: الإحصاءات, 2: الرحلة, 3: الإعدادات
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.WEEK)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _weekOffset = MutableStateFlow(0L)
    val weekOffset: StateFlow<Long> = _weekOffset.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.None)
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    // Reminder state
    private val _isReminderEnabled = MutableStateFlow(prefs.getBoolean("reminder_enabled", true))
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

    private val _reminderHour = MutableStateFlow(prefs.getInt("reminder_hour", 21))
    val reminderHour: StateFlow<Int> = _reminderHour.asStateFlow()

    private val _reminderMinute = MutableStateFlow(prefs.getInt("reminder_minute", 0))
    val reminderMinute: StateFlow<Int> = _reminderMinute.asStateFlow()

    // Persistent Database Flows
    val allHabits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogs: StateFlow<List<HabitLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedDaysCount: StateFlow<Int> = repository.completedDaysCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalLogsCount: StateFlow<Int> = repository.totalRecordedLogsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filtered habits according to search and favorite filters
    val filteredHabits: StateFlow<List<Habit>> = combine(
        allHabits,
        _searchQuery,
        _onlyFavorites
    ) { habits, query, favoritesOnly ->
        habits.filter { habit ->
            val matchesQuery = query.isBlank() ||
                    habit.title.contains(query, ignoreCase = true) ||
                    habit.description.contains(query, ignoreCase = true)
            val matchesFav = !favoritesOnly || habit.isFavorite
            matchesQuery && matchesFav
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // High performance pre-calculated UI models running on Dispatchers.Default
    val habitCardUiModels: StateFlow<List<HabitCardUiModel>> = combine(
        filteredHabits,
        allLogs,
        _viewMode,
        _weekOffset
    ) { habits, logs, viewMode, weekOffset ->
        withContext(Dispatchers.Default) {
            val dates = when (viewMode) {
                ViewMode.WEEK, ViewMode.GRID -> DateUtils.getDaysForWeek(weekOffset)
                ViewMode.MONTH -> DateUtils.getDaysForMonth(weekOffset)
            }
            val logsByHabit = logs.groupBy { it.habitId }
                .mapValues { entry -> entry.value.associateBy { it.date } }

            habits.map { habit ->
                val logsMap = logsByHabit[habit.id] ?: emptyMap()
                val daySummaries = DateUtils.getDaySummariesForHabit(habit, dates, logsMap)
                val streak = DateUtils.calculateStreakFast(logsMap)
                val completionRate = DateUtils.calculateCompletionRateFast(habit, logsMap)
                val color = try {
                    Color(android.graphics.Color.parseColor(habit.colorHex))
                } catch (e: Exception) {
                    StreqaTeal
                }
                val todaySummary = daySummaries.find { it.isToday }
                val isTodayDone = todaySummary?.status == HabitStatus.DONE
                val isTodayScheduled = todaySummary?.isScheduledDay ?: true

                HabitCardUiModel(
                    habit = habit,
                    themeColor = color,
                    daySummaries = daySummaries,
                    streakDays = streak,
                    completionRate = completionRate,
                    isTodayDone = isTodayDone,
                    isTodayScheduled = isTodayScheduled,
                    todaySummary = todaySummary
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun setWeekOffset(offset: Long) {
        _weekOffset.value = offset
    }

    fun nextWeek() {
        _weekOffset.value += 1
    }

    fun previousWeek() {
        _weekOffset.value -= 1
    }

    fun resetToCurrentWeek() {
        _weekOffset.value = 0
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearchActive() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) {
            _searchQuery.value = ""
        }
    }

    fun toggleFavoritesOnly() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun showAddHabitDialog() {
        _dialogState.value = DialogState.AddHabit
    }

    fun showEditHabitDialog(habit: Habit) {
        _dialogState.value = DialogState.EditHabit(habit)
    }

    fun showDayNoteDialog(habit: Habit, daySummary: DaySummary) {
        _dialogState.value = DialogState.DayNote(habit, daySummary)
    }

    fun showDeleteConfirmDialog(habit: Habit) {
        _dialogState.value = DialogState.DeleteConfirm(habit)
    }

    fun dismissDialog() {
        _dialogState.value = DialogState.None
    }

    fun toggleHabitDay(habitId: Long, date: String) {
        viewModelScope.launch {
            repository.toggleDayStatus(habitId, date)
        }
    }

    fun saveDayNoteAndStatus(habitId: Long, date: String, status: HabitStatus, notes: String) {
        viewModelScope.launch {
            repository.setDayStatusAndNote(habitId, date, status, notes)
            dismissDialog()
        }
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            if (habit.id == 0L) {
                repository.insertHabit(habit)
            } else {
                repository.updateHabit(habit)
            }
            dismissDialog()
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            dismissDialog()
        }
    }

    fun toggleFavorite(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit.copy(isFavorite = !habit.isFavorite))
        }
    }

    fun updateReminderConfig(enabled: Boolean, hour: Int, minute: Int) {
        _isReminderEnabled.value = enabled
        _reminderHour.value = hour
        _reminderMinute.value = minute

        prefs.edit()
            .putBoolean("reminder_enabled", enabled)
            .putInt("reminder_hour", hour)
            .putInt("reminder_minute", minute)
            .apply()

        val context = getApplication<Application>()
        if (enabled) {
            ReminderScheduler.scheduleDailyReminder(context, hour, minute)
        } else {
            ReminderScheduler.cancelReminder(context)
        }
    }

    fun sendTestNotification() {
        val context = getApplication<Application>()
        NotificationHelper.showReminderNotification(
            context = context,
            title = "تجربة إشعار تذكير Streqa ✨",
            message = "هكذا سيصلك التنبيه اليومي لتسجيل إنجازاتك والحفاظ على السلسلة!"
        )
    }
}
