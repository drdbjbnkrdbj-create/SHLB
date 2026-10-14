package com.example.data.repository

import com.example.data.local.HabitDao
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HabitStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HabitRepository(private val habitDao: HabitDao) {

    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allLogs: Flow<List<HabitLog>> = habitDao.getAllLogs()
    val completedDaysCount: Flow<Int> = habitDao.countCompletedDays()
    val totalRecordedLogsCount: Flow<Int> = habitDao.countTotalRecordedLogs()

    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>> = habitDao.getLogsForHabit(habitId)

    suspend fun insertHabit(habit: Habit): Long = withContext(Dispatchers.IO) {
        habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habitId: Long) = withContext(Dispatchers.IO) {
        habitDao.deleteHabitById(habitId)
        habitDao.deleteLogsForHabit(habitId)
    }

    suspend fun toggleDayStatus(habitId: Long, date: String) = withContext(Dispatchers.IO) {
        val existing = habitDao.getLog(habitId, date)
        val nextStatus = when (existing?.status) {
            null, HabitStatus.NONE -> HabitStatus.DONE
            HabitStatus.DONE -> HabitStatus.FAILED
            HabitStatus.FAILED -> HabitStatus.NONE
        }
        val note = existing?.notes ?: ""
        habitDao.upsertLog(
            HabitLog(
                id = existing?.id ?: 0,
                habitId = habitId,
                date = date,
                status = nextStatus,
                notes = note,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun setDayStatusAndNote(
        habitId: Long,
        date: String,
        status: HabitStatus,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val existing = habitDao.getLog(habitId, date)
        habitDao.upsertLog(
            HabitLog(
                id = existing?.id ?: 0,
                habitId = habitId,
                date = date,
                status = status,
                notes = notes,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun getLogForDate(habitId: Long, date: String): HabitLog? = withContext(Dispatchers.IO) {
        habitDao.getLog(habitId, date)
    }
}
