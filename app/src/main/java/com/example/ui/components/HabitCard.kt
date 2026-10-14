package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DaySummary
import com.example.ui.model.HabitCardUiModel
import com.example.ui.theme.StreqaCardBg
import com.example.ui.theme.StreqaCardBorder
import com.example.ui.theme.StreqaTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HabitCard(
    item: HabitCardUiModel,
    onToggleDay: (String) -> Unit,
    onOpenDayNote: (DaySummary) -> Unit,
    onEditHabit: () -> Unit,
    onDeleteHabit: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val habit = item.habit
    val themeColor = item.themeColor
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = StreqaCardBg),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, StreqaCardBorder, RoundedCornerShape(22.dp))
            .testTag("habit_card_${habit.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: 3-dots on left, Title + Subtitle + Icon on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 3-dots Menu & Favorite
                Box {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF242C34))
                                .clickable { menuExpanded = true }
                                .testTag("habit_menu_btn_${habit.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "خيارات العادة",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (habit.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "المفضلة",
                                tint = if (habit.isFavorite) Color(0xFFFBBF24) else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color(0xFF20272F))
                    ) {
                        item.todaySummary?.let { todaySum ->
                            DropdownMenuItem(
                                text = { Text("تدوين ملاحظة اليوم 📝", color = TextPrimary) },
                                leadingIcon = {
                                    Icon(Icons.Default.EditNote, contentDescription = null, tint = StreqaTeal)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onOpenDayNote(todaySum)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("تعديل العادة", color = TextPrimary) },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = StreqaTeal)
                            },
                            onClick = {
                                menuExpanded = false
                                onEditHabit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("حذف العادة", color = Color(0xFFEF4444)) },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                            },
                            onClick = {
                                menuExpanded = false
                                onDeleteHabit()
                            }
                        )
                    }
                }

                // Title + Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = habit.title,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = habit.iconEmoji,
                                fontSize = 18.sp
                            )
                        }
                        if (habit.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = habit.description,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.End
                            )
                        }
                    }

                    // Habit Icon Badge with tinted background
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(themeColor.copy(alpha = 0.18f))
                            .border(1.dp, themeColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    ) {
                        Text(
                            text = habit.iconEmoji,
                            fontSize = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Badges row: Status pill, Frequency pill, and Streak badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Today Status badge
                if (item.isTodayDone) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeColor.copy(alpha = 0.2f))
                            .border(1.dp, themeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable {
                                item.todaySummary?.let { onOpenDayNote(it) }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تم اليوم",
                            color = themeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else if (!item.isTodayScheduled) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF222C34))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "غير مجدول لليوم",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF263038))
                            .clickable {
                                item.todaySummary?.let { onOpenDayNote(it) }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "في انتظار الإنجاز",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Schedule and Streak info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Frequency Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF242C34))
                            .border(1.dp, Color(0xFF2E3842), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = habit.frequencyLabel,
                            color = themeColor.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Streak pill (e.g. "٢ ي")
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF242C34))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${item.streakDays} ي",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Days horizontal row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.daySummaries.forEach { summary ->
                    DayStatusCircle(
                        daySummary = summary,
                        themeColor = themeColor,
                        onToggle = { onToggleDay(summary.dateStr) },
                        onOpenNote = { onOpenDayNote(summary) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Progress Bar & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(item.completionRate * 100).toInt()}% نسبة الإنجاز",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF242C34))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(item.completionRate.coerceIn(0.04f, 1f))
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(themeColor)
                    )
                }
            }
        }
    }
}
