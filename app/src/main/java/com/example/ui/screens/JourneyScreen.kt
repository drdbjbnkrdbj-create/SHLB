package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DaySummary
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HabitStatus
import com.example.ui.theme.StreqaCardBg
import com.example.ui.theme.StreqaCardBorder
import com.example.ui.theme.StreqaDarkBg
import com.example.ui.theme.StreqaTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun JourneyScreen(
    habits: List<Habit>,
    logs: List<HabitLog>,
    onToggleDay: (Long, String) -> Unit,
    onOpenDayNote: (Habit, DaySummary) -> Unit,
    onAddHabitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logsByHabit = remember(logs) {
        logs.groupBy { it.habitId }
            .mapValues { entry -> entry.value.associateBy { it.date } }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .background(StreqaDarkBg)
    ) {
        // Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StreqaTeal)
                            .clickable(onClick = onAddHabitClick)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "تحدي جديد",
                            tint = Color(0xFF042F2E),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "رحلة التحديات والنطاق الزمني 🏆",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "متابعة شاملة لجميع أيام التحدي من البداية حتى النهاية",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (habits.isEmpty()) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🏔️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد تحديات حالياً",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "ابدأ تحدي جديد بجدول زمني مخصص (مثلاً 21 أو 30 يوم)",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                val habitLogs = logsByHabit[habit.id] ?: emptyMap()
                ChallengeCard(
                    habit = habit,
                    logsMap = habitLogs,
                    onToggleDay = { dateStr -> onToggleDay(habit.id, dateStr) },
                    onOpenDayNote = { summary -> onOpenDayNote(habit, summary) }
                )
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    habit: Habit,
    logsMap: Map<String, HabitLog>,
    onToggleDay: (String) -> Unit,
    onOpenDayNote: (DaySummary) -> Unit
) {
    var expandedTimeline by remember { mutableStateOf(false) }

    val themeColor = remember(habit.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(habit.colorHex))
        } catch (e: Exception) {
            StreqaTeal
        }
    }

    val today = remember { DateUtils.today() }
    val startDate = remember(habit.startDate) {
        try { LocalDate.parse(habit.startDate, DateUtils.ISO_FORMATTER) } catch (e: Exception) { today.minusDays(14) }
    }
    val endDate = remember(habit.endDate) {
        try { LocalDate.parse(habit.endDate, DateUtils.ISO_FORMATTER) } catch (e: Exception) { today.plusDays(16) }
    }

    val totalDays = remember(startDate, endDate) {
        (ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1).coerceAtLeast(1)
    }

    val daysElapsed = remember(startDate, today) {
        if (today.isBefore(startDate)) 0
        else (ChronoUnit.DAYS.between(startDate, today).toInt() + 1).coerceAtMost(totalDays)
    }

    val daysRemaining = (totalDays - daysElapsed).coerceAtLeast(0)

    val allChallengeDays = remember(habit.startDate, habit.endDate) {
        DateUtils.getDaysInRange(habit.startDate, habit.endDate)
    }

    val completedDaysCount = remember(habit.id, logsMap) {
        logsMap.values.count { it.status == HabitStatus.DONE }
    }

    val progressFraction = (completedDaysCount.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = StreqaCardBg),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StreqaCardBorder, RoundedCornerShape(22.dp))
            .testTag("challenge_card_${habit.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Card Top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Days left badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF222C35))
                        .border(1.dp, Color(0xFF2C3845), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (daysRemaining > 0) "متبقي $daysRemaining يوم" else "انتهى التحدي 🏁",
                        color = if (daysRemaining > 0) StreqaTeal else Color(0xFF34D399),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Title + Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${habit.title} ${habit.iconEmoji}",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${DateUtils.formatReadableDateArabic(habit.startDate)}  ←  ${DateUtils.formatReadableDateArabic(habit.endDate)}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar and days count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$completedDaysCount من $totalDays يوم منجز",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "${(progressFraction * 100).toInt()}% مكتمل",
                    color = themeColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF242C34))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction.coerceIn(0.03f, 1f))
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(themeColor)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Toggle timeline expansion button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E262E))
                    .clickable { expandedTimeline = !expandedTimeline }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (expandedTimeline) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
                Text(
                    text = if (expandedTimeline) "إخفاء جدول أيام التحدي" else "عرض جدول الأيام التلقائي (${allChallengeDays.size} يوم)",
                    color = StreqaTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Expanded Timeline: Ordered Days List
            AnimatedVisibility(visible = expandedTimeline) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val daySummaries = remember(allChallengeDays, logsMap) {
                        DateUtils.getDaySummariesForHabit(habit, allChallengeDays, logsMap)
                    }

                    daySummaries.forEach { summary ->
                        val isDone = summary.status == HabitStatus.DONE
                        val isFailed = summary.status == HabitStatus.FAILED

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (summary.isToday) Color(0xFF25313D) else Color(0xFF192026))
                                .clickable { onToggleDay(summary.dateStr) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status Icon Button
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDone -> themeColor
                                            isFailed -> Color(0xFFEF4444)
                                            else -> Color(0xFF263038)
                                        }
                                    )
                            ) {
                                when {
                                    isDone -> Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF042F2E), modifier = Modifier.size(16.dp))
                                    isFailed -> Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    else -> Text(text = "—", color = TextMuted, fontSize = 11.sp)
                                }
                            }

                            // Day details: Day of week + formatted date
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (summary.isToday) {
                                        Text(
                                            text = "(اليوم) ",
                                            color = StreqaTeal,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "${summary.dayOfWeekNameArabic} - ${summary.dayOfMonth} ${DateUtils.getArabicMonthName(DateUtils.parseDateFast(summary.dateStr).monthValue)}",
                                        color = if (summary.isToday) TextPrimary else TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = if (summary.isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                if (summary.note.isNotBlank()) {
                                    Text(
                                        text = "ملاحظة: ${summary.note}",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
