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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DaySummary
import com.example.data.model.Habit
import com.example.ui.ViewMode
import com.example.ui.components.HabitCard
import com.example.ui.model.HabitCardUiModel
import com.example.ui.theme.StreqaCardBorder
import com.example.ui.theme.StreqaDarkBg
import com.example.ui.theme.StreqaTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@Composable
fun HabitsScreen(
    habitUiModels: List<HabitCardUiModel>,
    viewMode: ViewMode,
    weekOffset: Long,
    searchQuery: String,
    isSearchActive: Boolean,
    onlyFavorites: Boolean,
    onViewModeChange: (ViewMode) -> Unit,
    onWeekOffsetChange: (Long) -> Unit,
    onNextWeek: () -> Unit,
    onPrevWeek: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onToggleFavorites: () -> Unit,
    onAddHabitClick: () -> Unit,
    onToggleDay: (Long, String) -> Unit,
    onOpenDayNote: (Habit, DaySummary) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onToggleFavorite: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    // Week date range label calculated only when viewMode or weekOffset changes
    val weekRangeLabel = remember(viewMode, weekOffset) {
        val dates = when (viewMode) {
            ViewMode.WEEK, ViewMode.GRID -> DateUtils.getDaysForWeek(weekOffset)
            ViewMode.MONTH -> DateUtils.getDaysForMonth(weekOffset)
        }
        if (dates.isNotEmpty()) {
            val first = dates.first()
            val last = dates.last()
            val firstStr = "${first.dayOfMonth} ${DateUtils.getArabicMonthName(first.monthValue)}"
            val lastStr = "${last.dayOfMonth} ${DateUtils.getArabicMonthName(last.monthValue)}"
            if (weekOffset == 0L) "الأسبوع الحالي ($firstStr - $lastStr)" else "$firstStr - $lastStr"
        } else {
            "الأسبوع"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StreqaDarkBg)
    ) {
        // Top Header Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Action Buttons on Left (+, Star, Search)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Add Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StreqaTeal)
                            .clickable(onClick = onAddHabitClick)
                            .testTag("add_habit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إضافة عادة",
                            tint = Color(0xFF042F2E),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Favorites filter button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (onlyFavorites) Color(0xFF2D3845) else Color(0xFF1E262E))
                            .border(1.dp, if (onlyFavorites) Color(0xFFFBBF24) else StreqaCardBorder, RoundedCornerShape(12.dp))
                            .clickable(onClick = onToggleFavorites)
                            .testTag("filter_favorites_btn")
                    ) {
                        Icon(
                            imageVector = if (onlyFavorites) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "المفضلة فقط",
                            tint = if (onlyFavorites) Color(0xFFFBBF24) else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Search toggle button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSearchActive) Color(0xFF2D3845) else Color(0xFF1E262E))
                            .border(1.dp, StreqaCardBorder, RoundedCornerShape(12.dp))
                            .clickable(onClick = onToggleSearch)
                            .testTag("search_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = if (isSearchActive) StreqaTeal else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Brand Title on Right
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Streqa",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pro",
                        color = StreqaTeal,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Search input field
            AnimatedVisibility(visible = isSearchActive) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("ابحث عن عادة...", color = TextMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "مسح", tint = TextSecondary)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StreqaTeal,
                        unfocusedBorderColor = StreqaCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = StreqaTeal
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_habits_input")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // View Selector Bar: [ شبكة ] [ أسبوع ] [ شهر ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF182026))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    ViewMode.GRID to "شبكة",
                    ViewMode.WEEK to "أسبوع",
                    ViewMode.MONTH to "شهر"
                ).forEach { (mode, label) ->
                    val isSelected = viewMode == mode
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) StreqaTeal else Color.Transparent)
                            .clickable { onViewModeChange(mode) }
                            .padding(vertical = 8.dp)
                            .testTag("view_mode_${mode.name.lowercase()}")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFF042F2E) else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // Week Navigator (Previous / Next Week)
            if (viewMode == ViewMode.WEEK) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPrevWeek,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الأسبوع السابق",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = weekRangeLabel,
                        color = if (weekOffset == 0L) StreqaTeal else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    IconButton(
                        onClick = onNextWeek,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "الأسبوع القادم",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Habit Cards List - Ultra-fast LazyColumn with zero computations during scroll
        if (habitUiModels.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🎯",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "لا توجد عادات تطابق البحث" else "لم تقم بإضافة عادات بعد",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اضغط على زر (+) في الأعلى لبدء تحدي جديد وبناء عادة راسخة!",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("habits_list")
            ) {
                items(
                    items = habitUiModels,
                    key = { it.habit.id }
                ) { item ->
                    HabitCard(
                        item = item,
                        onToggleDay = { dateStr -> onToggleDay(item.habit.id, dateStr) },
                        onOpenDayNote = { summary -> onOpenDayNote(item.habit, summary) },
                        onEditHabit = { onEditHabit(item.habit) },
                        onDeleteHabit = { onDeleteHabit(item.habit) },
                        onToggleFavorite = { onToggleFavorite(item.habit) }
                    )
                }
            }
        }
    }
}
