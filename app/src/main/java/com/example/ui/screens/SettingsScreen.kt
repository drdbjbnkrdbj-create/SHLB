package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StreqaCardBg
import com.example.ui.theme.StreqaCardBorder
import com.example.ui.theme.StreqaDarkBg
import com.example.ui.theme.StreqaTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    isReminderEnabled: Boolean,
    reminderHour: Int,
    reminderMinute: Int,
    habitsCount: Int,
    totalLogsCount: Int,
    onUpdateReminder: (Boolean, Int, Int) -> Unit,
    onTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedHour by remember(reminderHour) { mutableIntStateOf(reminderHour) }
    var selectedMinute by remember(reminderMinute) { mutableIntStateOf(reminderMinute) }

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
                    text = "الإعدادات والتذكيرات ⚙️",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تخصيص المنبه اليومي وضمان بقاء بياناتك وسجلاتك بشكل دائم",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Notification & Reminders Section
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isReminderEnabled,
                            onCheckedChange = { enabled ->
                                onUpdateReminder(enabled, selectedHour, selectedMinute)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF042F2E),
                                checkedTrackColor = StreqaTeal,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = Color(0xFF242C34)
                            ),
                            modifier = Modifier.testTag("reminder_switch")
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "التذكير اليومي (Notifications)",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "تنبيه يومي لتسجيل إنجازاتك والحفاظ على السلسلة",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StreqaTeal.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = StreqaTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (isReminderEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "موعد التذكير المفضل:",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Time Presets
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(
                                Pair(20, 0) to "08:00 م",
                                Pair(21, 0) to "09:00 م",
                                Pair(22, 0) to "10:00 م",
                                Pair(7, 0) to "07:00 ص"
                            ).forEach { (time, label) ->
                                val isSelected = selectedHour == time.first && selectedMinute == time.second
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) StreqaTeal else Color(0xFF222B33))
                                        .border(
                                            1.dp,
                                            if (isSelected) StreqaTeal else StreqaCardBorder,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedHour = time.first
                                            selectedMinute = time.second
                                            onUpdateReminder(true, time.first, time.second)
                                            Toast.makeText(context, "تم ضبط المنبه اليومي الساعة $label", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color(0xFF042F2E) else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Test Notification Button
                        Button(
                            onClick = onTestNotification,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222C35)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .border(1.dp, StreqaCardBorder, RoundedCornerShape(12.dp))
                                .testTag("test_notification_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = StreqaTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "إرسال إشعار تجريبي الآن",
                                color = StreqaTeal,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Local Storage Persistence Guarantee Section
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "حفظ البيانات الموثوق (Local Storage)",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "حفظ دائم في قاعدة بيانات Room SQLite المحلية",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF34D399).copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notice box explaining persistence vs cache
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF13231E))
                            .border(1.dp, Color(0xFF059669).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "بياناتك، عاداتك، السلاسل، والملاحظات اليومية مخزنة بالكامل في مساحة التخزين الداخلية الدائمة للهاتف. لن يتم حذفها أبداً حتى لو قام المستخدم بمسح ذاكرة التخزين المؤقت (الكاش - Cache).",
                                color = Color(0xFFD1FAE5),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Storage stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$habitsCount",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "عادات محفوظة",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(30.dp)
                                .background(StreqaCardBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalLogsCount",
                                color = StreqaTeal,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "سجل يومي مسجل",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(30.dp)
                                .background(StreqaCardBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "نشط ومؤمن",
                                color = Color(0xFF34D399),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "حالة قاعدة البيانات",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // About & Version
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
                        text = "عن التطبيق 💡",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "قال صلى الله عليه وسلم: المؤمن القوي خير واحب الى الله من المؤمن الضعيف وفي كلن خير. صمم هذاالتطبيق لمساعدتك في بنأوتصحيح العادات.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "الإصدار: 1.0.0 · تم التطوير من قبل المطور الاعظم خطاب وفق معايير الدولة الاسلاميةMaterial Design 3 ",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
