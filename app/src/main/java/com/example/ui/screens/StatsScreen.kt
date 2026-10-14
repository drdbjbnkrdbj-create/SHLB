package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HabitStatus
import com.example.ui.theme.StreqaAmber
import com.example.ui.theme.StreqaCardBg
import com.example.ui.theme.StreqaCardBorder
import com.example.ui.theme.StreqaDarkBg
import com.example.ui.theme.StreqaTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@Composable
fun StatsScreen(
    habits: List<Habit>,
    logs: List<HabitLog>,
    completedDaysCount: Int,
    modifier: Modifier = Modifier
) {
    val logsByHabit = remember(logs) {
        logs.groupBy { it.habitId }
            .mapValues { entry -> entry.value.associateBy { it.date } }
    }

    val totalDoneLogs = remember(logs) {
        logs.count { it.status == HabitStatus.DONE }
    }

    val totalFailedLogs = remember(logs) {
        logs.count { it.status == HabitStatus.FAILED }
    }

    val maxStreak = remember(habits, logsByHabit) {
        habits.maxOfOrNull { habit ->
            val habitLogs = logsByHabit[habit.id] ?: emptyMap()
            DateUtils.calculateStreakFast(habitLogs)
        } ?: 0
    }

    val overallRate = remember(totalDoneLogs, totalFailedLogs) {
        val total = totalDoneLogs + totalFailedLogs
        if (total > 0) (totalDoneLogs.toFloat() / total.toFloat()) else 0f
    }

    // Recent logs with notes
    val notesWithLogs = remember(logs, habits) {
        val habitsMap = habits.associateBy { it.id }
        logs.filter { it.notes.isNotBlank() }
            .sortedByDescending { it.updatedAt }
            .take(6)
            .map { log ->
                val habit = habitsMap[log.habitId]
                Triple(habit, log, DateUtils.formatReadableDateArabic(log.date))
            }
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
                Text(
                    text = "الإحصاءات والتحليلات 📊",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "نظرة تفصيلية على مسيرتك، الالتزام، ونسبة الإنجاز الإجمالية",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // 2x2 Grid of Key Metrics Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "الأيام المنجزة",
                        value = "$totalDoneLogs يوم",
                        icon = Icons.Default.CheckCircle,
                        accentColor = StreqaTeal,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "أعلى سلسلة مستمرة",
                        value = "$maxStreak أيام",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = StreqaAmber,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "معدل الالتزام",
                        value = "${(overallRate * 100).toInt()}%",
                        icon = Icons.Default.TrendingUp,
                        accentColor = Color(0xFF60A5FA),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "العادات النشطة",
                        value = "${habits.size} عادة",
                        icon = Icons.Default.EmojiEvents,
                        accentColor = Color(0xFFF472B6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Breakdown per habit
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StreqaCardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StreqaCardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "نسب الإنجاز لكل عادة",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (habits.isEmpty()) {
                        Text(
                            text = "لا توجد عادات مسجلة حالياً",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    } else {
                        habits.forEach { habit ->
                            val habitLogs = logsByHabit[habit.id] ?: emptyMap()
                            val rate = DateUtils.calculateCompletionRateFast(habit, habitLogs)
                            val streak = DateUtils.calculateStreakFast(habitLogs)
                            val color = try {
                                Color(android.graphics.Color.parseColor(habit.colorHex))
                            } catch (e: Exception) {
                                StreqaTeal
                            }

                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = habit.iconEmoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = habit.title,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "سلسلة $streak ي",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${(rate * 100).toInt()}%",
                                            color = color,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF242C34))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(rate.coerceIn(0.02f, 1f))
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Notes & Reflection Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StreqaCardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StreqaCardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "سجل الملاحظات والأسباب الأخيرة 📝",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "الأسباب والملاحظات اليومية التي قمت بتسجيلها للإنجاز أو الإخفاق",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (notesWithLogs.isEmpty()) {
                        Text(
                            text = "لا توجد ملاحظات مدونة بعد. اضغط مطولاً على أي يوم لتسجيل سبب النجاح أو الإخفاق!",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    } else {
                        notesWithLogs.forEach { (habit, log, dateLabel) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF222B33))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (log.status == HabitStatus.DONE) StreqaTeal.copy(alpha = 0.2f)
                                            else Color(0xFFEF4444).copy(alpha = 0.2f)
                                        )
                                ) {
                                    Text(
                                        text = if (log.status == HabitStatus.DONE) "✓" else "✕",
                                        color = if (log.status == HabitStatus.DONE) StreqaTeal else Color(0xFFEF4444),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = habit?.let { "${it.title} ${it.iconEmoji}" } ?: "عادة",
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = dateLabel,
                                            color = StreqaTeal,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = log.notes,
                                        color = TextSecondary,
                                        fontSize = 12.sp
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

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = StreqaCardBg),
        modifier = modifier.border(1.dp, StreqaCardBorder, RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
