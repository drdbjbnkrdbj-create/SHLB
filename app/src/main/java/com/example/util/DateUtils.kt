package com.example.util

import com.example.data.model.DaySummary
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HabitStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

object DateUtils {
    val ISO_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val ARABIC_DAYS = mapOf(
        DayOfWeek.SUNDAY to "الأحد",
        DayOfWeek.MONDAY to "الاثنين",
        DayOfWeek.TUESDAY to "الثلاثاء",
        DayOfWeek.WEDNESDAY to "الأربعاء",
        DayOfWeek.THURSDAY to "الخميس",
        DayOfWeek.FRIDAY to "الجمعة",
        DayOfWeek.SATURDAY to "السبت"
    )

    private val ARABIC_MONTHS = arrayOf(
        "", "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    private val parsedDatesCache = ConcurrentHashMap<String, LocalDate>()

    fun parseDateFast(dateStr: String): LocalDate {
        return parsedDatesCache.computeIfAbsent(dateStr) {
            try {
                LocalDate.parse(it, ISO_FORMATTER)
            } catch (e: Exception) {
                LocalDate.now()
            }
        }
    }

    fun today(): LocalDate = LocalDate.now()

    fun todayStr(): String = today().format(ISO_FORMATTER)

    fun getArabicDayName(date: LocalDate): String {
        return ARABIC_DAYS[date.dayOfWeek] ?: "يوم"
    }

    fun getArabicMonthName(monthNumber: Int): String {
        return if (monthNumber in 1..12) ARABIC_MONTHS[monthNumber] else ""
    }

    fun formatReadableDateArabic(dateStr: String): String {
        return try {
            val date = parseDateFast(dateStr)
            "${getArabicDayName(date)} ${date.dayOfMonth} ${getArabicMonthName(date.monthValue)}"
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getDaysForWeek(weekOffset: Long = 0): List<LocalDate> {
        val current = today().plusWeeks(weekOffset)
        val diffToSunday = (current.dayOfWeek.value % 7) // Sunday=7 -> 0, Monday=1 -> 1
        val sunday = current.minusDays(diffToSunday.toLong())
        return (0..6).map { sunday.plusDays(it.toLong()) }
    }

    fun getDaysForMonth(monthOffset: Long = 0): List<LocalDate> {
        val currentMonth = today().plusMonths(monthOffset)
        val firstDay = currentMonth.withDayOfMonth(1)
        val length = currentMonth.lengthOfMonth()
        return (0 until length).map { firstDay.plusDays(it.toLong()) }
    }

    fun getDaysInRange(startDateStr: String, endDateStr: String): List<LocalDate> {
        return try {
            val start = parseDateFast(startDateStr)
            val end = parseDateFast(endDateStr)
            val daysBetween = ChronoUnit.DAYS.between(start, end)
            if (daysBetween < 0 || daysBetween > 365) {
                listOf(start)
            } else {
                (0..daysBetween).map { start.plusDays(it) }
            }
        } catch (e: Exception) {
            listOf(today())
        }
    }

    fun getDaySummariesForHabit(
        habit: Habit,
        dates: List<LocalDate>,
        logsMap: Map<String, HabitLog>
    ): List<DaySummary> {
        val today = today()
        val start = parseDateFast(habit.startDate)
        val end = parseDateFast(habit.endDate)

        val scheduledDayIndices = habit.scheduledDays.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()

        return dates.map { date ->
            val dateStr = date.format(ISO_FORMATTER)
            val dayOfWeekVal = if (date.dayOfWeek == DayOfWeek.SUNDAY) 1 else date.dayOfWeek.value + 1
            val isScheduled = scheduledDayIndices.isEmpty() || scheduledDayIndices.contains(dayOfWeekVal)
            val log = logsMap[dateStr]
            val status = log?.status ?: HabitStatus.NONE
            val note = log?.notes ?: ""

            DaySummary(
                dateStr = dateStr,
                dayOfWeekNameArabic = getArabicDayName(date),
                dayOfMonth = date.dayOfMonth,
                isToday = date.isEqual(today),
                isFuture = date.isAfter(today),
                isWithinRange = !date.isBefore(start) && !date.isAfter(end),
                isScheduledDay = isScheduled,
                status = status,
                note = note
            )
        }
    }

    // High performance O(1) streak calculation using pre-indexed map
    fun calculateStreakFast(logsMap: Map<String, HabitLog>): Int {
        var streak = 0
        var checkDate = today()

        val todayStr = checkDate.format(ISO_FORMATTER)
        val isTodayDone = logsMap[todayStr]?.status == HabitStatus.DONE

        if (isTodayDone) {
            streak++
            checkDate = checkDate.minusDays(1)
        } else {
            val yesterdayStr = checkDate.minusDays(1).format(ISO_FORMATTER)
            if (logsMap[yesterdayStr]?.status == HabitStatus.DONE) {
                streak++
                checkDate = checkDate.minusDays(2)
            } else {
                return 0
            }
        }

        while (true) {
            val dateStr = checkDate.format(ISO_FORMATTER)
            if (logsMap[dateStr]?.status == HabitStatus.DONE) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    // Legacy method for backward compatibility
    fun calculateStreak(habitId: Long, logs: List<HabitLog>): Int {
        val map = logs.filter { it.habitId == habitId }.associateBy { it.date }
        return calculateStreakFast(map)
    }

    // High performance completion rate
    fun calculateCompletionRateFast(
        habit: Habit,
        logsMap: Map<String, HabitLog>
    ): Float {
        val start = parseDateFast(habit.startDate)
        val today = today()
        val effectiveEnd = if (today.isBefore(start)) start else today

        val daysCount = ChronoUnit.DAYS.between(start, effectiveEnd).toInt() + 1
        if (daysCount <= 0) return 0f

        val completedCount = logsMap.values.count { it.status == HabitStatus.DONE }
        return (completedCount.toFloat() / daysCount.toFloat()).coerceIn(0f, 1f)
    }

    fun calculateCompletionRate(
        habit: Habit,
        logs: List<HabitLog>
    ): Float {
        val map = logs.filter { it.habitId == habit.id }.associateBy { it.date }
        return calculateCompletionRateFast(habit, map)
    }
}
