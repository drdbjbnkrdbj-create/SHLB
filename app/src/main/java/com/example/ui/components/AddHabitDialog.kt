package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Habit
import com.example.ui.theme.StreqaCardBg
import com.example.ui.theme.StreqaCardBorder
import com.example.ui.theme.StreqaDarkBg
import com.example.ui.theme.StreqaTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddHabitDialog(
    initialHabit: Habit? = null,
    onDismiss: () -> Unit,
    onSaveHabit: (Habit) -> Unit
) {
    var title by remember { mutableStateOf(initialHabit?.title ?: "") }
    var description by remember { mutableStateOf(initialHabit?.description ?: "") }
    var selectedEmoji by remember { mutableStateOf(initialHabit?.iconEmoji ?: "✨") }
    var selectedColorHex by remember { mutableStateOf(initialHabit?.colorHex ?: "#2DD4BF") }

    // Range days duration preset (7, 21, 30, 60 days)
    var durationDays by remember { mutableIntStateOf(30) }
    var selectedFrequency by remember { mutableStateOf(initialHabit?.frequencyLabel ?: "يومي") }

    val emojis = listOf(
        "✨", "🏋️‍♂️", "📚", "☀️", "🌙", "💸", "🪴", "🧹",
        "💖", "💧", "🏃", "🧘", "💻", "🎯", "🍎", "🚴"
    )

    val colors = listOf(
        "#2DD4BF", // Teal
        "#F472B6", // Pink
        "#FBBF24", // Amber
        "#60A5FA", // Blue
        "#34D399", // Emerald
        "#A78BFA", // Purple
        "#FB7185", // Rose
        "#F97316"  // Orange
    )

    val today = remember { LocalDate.now() }
    val startDateStr = remember { initialHabit?.startDate ?: today.format(DateUtils.ISO_FORMATTER) }
    val endDateStr = remember(durationDays) {
        initialHabit?.endDate ?: today.plusDays(durationDays.toLong()).format(DateUtils.ISO_FORMATTER)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = StreqaCardBg,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, StreqaCardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = TextSecondary)
                    }
                    Text(
                        text = if (initialHabit == null) "إضافة عادة جديدة" else "تعديل العادة",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title input
                Text(text = "اسم العادة أو التحدي", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("مثال: أذكار الصباح، تمرين، قراءة 20 صفحة", color = TextMuted) },
                    singleLine = true,
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
                        .testTag("habit_title_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Description input
                Text(text = "الوصف أو الهدف (اختياري)", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("مثال: تطوير نفسي، جسم صحي", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StreqaTeal,
                        unfocusedBorderColor = StreqaCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = StreqaTeal
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Emoji Picker
                Text(text = "اختر الأيقونة", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    emojis.forEach { emoji ->
                        val isSelected = emoji == selectedEmoji
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF2E3B46) else Color(0xFF161E24))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) StreqaTeal else StreqaCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedEmoji = emoji }
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color Picker
                Text(text = "لون العادة", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colors.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = hex == selectedColorHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Challenge Range presets (نظام النطاق الزمني والتحديات)
                Text(text = "نطاق التحدي والمدة", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(7 to "أسبوع", 21 to "21 يوم", 30 to "شهر", 60 to "60 يوم").forEach { (days, label) ->
                        val isSelected = durationDays == days
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) StreqaTeal else Color(0xFF1B2329))
                                .border(1.dp, if (isSelected) StreqaTeal else StreqaCardBorder, RoundedCornerShape(12.dp))
                                .clickable { durationDays = days }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color(0xFF0F172A) else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Frequency options
                Text(text = "التكرار", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        "يومي" to "1,2,3,4,5,6,7",
                        "أيام العمل" to "1,2,3,4,5",
                        "مرة أسبوعياً" to "7"
                    ).forEach { (label, _) ->
                        val isSelected = selectedFrequency == label
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF26323D) else Color(0xFF182026))
                                .border(1.dp, if (isSelected) StreqaTeal else StreqaCardBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedFrequency = label }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) StreqaTeal else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save button
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val scheduledDaysStr = when (selectedFrequency) {
                                "أيام العمل" -> "1,2,3,4,5"
                                "مرة أسبوعياً" -> "7"
                                else -> "1,2,3,4,5,6,7"
                            }
                            val updated = initialHabit?.copy(
                                title = title.trim(),
                                description = description.trim(),
                                iconEmoji = selectedEmoji,
                                colorHex = selectedColorHex,
                                frequencyLabel = selectedFrequency,
                                scheduledDays = scheduledDaysStr
                            ) ?: Habit(
                                title = title.trim(),
                                description = description.trim(),
                                iconEmoji = selectedEmoji,
                                colorHex = selectedColorHex,
                                startDate = startDateStr,
                                endDate = endDateStr,
                                frequencyLabel = selectedFrequency,
                                scheduledDays = scheduledDaysStr
                            )
                            onSaveHabit(updated)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StreqaTeal),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_habit_btn")
                ) {
                    Text(
                        text = if (initialHabit == null) "حفظ وبدء التحدي" else "تحديث العادة",
                        color = Color(0xFF042F2E),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
