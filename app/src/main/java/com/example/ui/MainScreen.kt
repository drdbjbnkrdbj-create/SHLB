package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddHabitDialog
import com.example.ui.components.DayNoteDialog
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.JourneyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.StreqaDarkBg
import com.example.ui.theme.StreqaTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val habitCardUiModels by viewModel.habitCardUiModels.collectAsStateWithLifecycle()
    val allHabits by viewModel.allHabits.collectAsStateWithLifecycle()
    val logs by viewModel.allLogs.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val weekOffset by viewModel.weekOffset.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val onlyFavorites by viewModel.onlyFavorites.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val isReminderEnabled by viewModel.isReminderEnabled.collectAsStateWithLifecycle()
    val reminderHour by viewModel.reminderHour.collectAsStateWithLifecycle()
    val reminderMinute by viewModel.reminderMinute.collectAsStateWithLifecycle()
    val completedDaysCount by viewModel.completedDaysCount.collectAsStateWithLifecycle()
    val totalLogsCount by viewModel.totalLogsCount.collectAsStateWithLifecycle()

    // Request POST_NOTIFICATIONS permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* handled */ }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val navItems = listOf(
        NavItem("اليوم", Icons.Filled.Check, Icons.Filled.Check, "nav_today"),
        NavItem("الإحصاءات", Icons.Filled.Equalizer, Icons.Outlined.Equalizer, "nav_stats"),
        NavItem("الرحلة", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_journey"),
        NavItem("الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
    )

    Scaffold(
        bottomBar = {
            // Custom Bottom Navigation matching screenshots
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1418))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF1E262E),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = currentTab == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.setTab(index) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag(item.tag)
                        ) {
                            if (isSelected) {
                                // Active Pill indicator (matching the teal pill in screenshot)
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(width = 54.dp, height = 30.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(StreqaTeal)
                                ) {
                                    Icon(
                                        imageVector = item.selectedIcon,
                                        contentDescription = item.label,
                                        tint = Color(0xFF042F2E),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            } else {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(width = 54.dp, height = 30.dp)
                                ) {
                                    Icon(
                                        imageVector = item.unselectedIcon,
                                        contentDescription = item.label,
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = item.label,
                                color = if (isSelected) TextPrimary else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        containerColor = StreqaDarkBg,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_fade") { tab ->
                when (tab) {
                    0 -> HabitsScreen(
                        habitUiModels = habitCardUiModels,
                        viewMode = viewMode,
                        weekOffset = weekOffset,
                        searchQuery = searchQuery,
                        isSearchActive = isSearchActive,
                        onlyFavorites = onlyFavorites,
                        onViewModeChange = viewModel::setViewMode,
                        onWeekOffsetChange = viewModel::setWeekOffset,
                        onNextWeek = viewModel::nextWeek,
                        onPrevWeek = viewModel::previousWeek,
                        onSearchQueryChange = viewModel::setSearchQuery,
                        onToggleSearch = viewModel::toggleSearchActive,
                        onToggleFavorites = viewModel::toggleFavoritesOnly,
                        onAddHabitClick = viewModel::showAddHabitDialog,
                        onToggleDay = viewModel::toggleHabitDay,
                        onOpenDayNote = { habit, summary ->
                            viewModel.showDayNoteDialog(habit, summary)
                        },
                        onEditHabit = viewModel::showEditHabitDialog,
                        onDeleteHabit = viewModel::showDeleteConfirmDialog,
                        onToggleFavorite = viewModel::toggleFavorite
                    )
                    1 -> StatsScreen(
                        habits = allHabits,
                        logs = logs,
                        completedDaysCount = completedDaysCount
                    )
                    2 -> JourneyScreen(
                        habits = allHabits,
                        logs = logs,
                        onToggleDay = viewModel::toggleHabitDay,
                        onOpenDayNote = { habit, summary ->
                            viewModel.showDayNoteDialog(habit, summary)
                        },
                        onAddHabitClick = viewModel::showAddHabitDialog
                    )
                    3 -> SettingsScreen(
                        isReminderEnabled = isReminderEnabled,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute,
                        habitsCount = allHabits.size,
                        totalLogsCount = totalLogsCount,
                        onUpdateReminder = viewModel::updateReminderConfig,
                        onTestNotification = viewModel::sendTestNotification
                    )
                }
            }
        }
    }

    // Dialogs handling
    when (val dialog = dialogState) {
        is DialogState.AddHabit -> {
            AddHabitDialog(
                initialHabit = null,
                onDismiss = viewModel::dismissDialog,
                onSaveHabit = viewModel::saveHabit
            )
        }
        is DialogState.EditHabit -> {
            AddHabitDialog(
                initialHabit = dialog.habit,
                onDismiss = viewModel::dismissDialog,
                onSaveHabit = viewModel::saveHabit
            )
        }
        is DialogState.DayNote -> {
            DayNoteDialog(
                habit = dialog.habit,
                daySummary = dialog.daySummary,
                onDismiss = viewModel::dismissDialog,
                onSave = { status, notes ->
                    viewModel.saveDayNoteAndStatus(dialog.habit.id, dialog.daySummary.dateStr, status, notes)
                }
            )
        }
        is DialogState.DeleteConfirm -> {
            AlertDialog(
                onDismissRequest = viewModel::dismissDialog,
                title = {
                    Text(
                        text = "حذف العادة؟",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "هل أنت متأكد من حذف \"${dialog.habit.title}\"؟ سيتم حذف جميع السلاسل والسجلات التابعة لها.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.deleteHabit(dialog.habit.id) },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Text("حذف", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = viewModel::dismissDialog,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("إلغاء")
                    }
                },
                containerColor = Color(0xFF1E262E),
                shape = RoundedCornerShape(20.dp)
            )
        }
        DialogState.None -> Unit
    }
}
