package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HabitStatus
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Streqa", appName)
    }

    @Test
    fun `test arabic day names and streak calculation`() {
        val today = DateUtils.today()
        val arabicName = DateUtils.getArabicDayName(today)
        assertTrue(arabicName.isNotBlank())

        val todayStr = today.format(DateUtils.ISO_FORMATTER)
        val yesterdayStr = today.minusDays(1).format(DateUtils.ISO_FORMATTER)

        val logs = listOf(
            HabitLog(habitId = 1, date = todayStr, status = HabitStatus.DONE),
            HabitLog(habitId = 1, date = yesterdayStr, status = HabitStatus.DONE)
        )

        val streak = DateUtils.calculateStreak(1, logs)
        assertEquals(2, streak)
    }
}
