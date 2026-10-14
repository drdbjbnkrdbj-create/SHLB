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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.example.data.model.DaySummary
import com.example.data.model.Habit
import com.example.data.model.HabitStatus
import com.example.ui.theme.StreqaCardBg
import com.example.ui.theme.StreqaCardBorder
import com.example.ui.theme.StreqaTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayNoteDialog(
    habit: Habit,
    daySummary: DaySummary,
    onDismiss: () -> Unit,
    onSave: (HabitStatus, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(daySummary.status) }
    var notes by remember { mutableStateOf(daySummary.note) }

    val quickSuccessReasons = listOf("التزام بالوقت ⏰", "طاقة ونشاط 💪", "تركيز كامل 🎯", "عادة راسخة 🌿")
    val quickFailureReasons = listOf("ضيق في الوقت ⏳", "إرهاق وتعب 😴", "انشغال مفاجئ 💼", "تسويف 🛋️")

    val readableDate = remember(daySummary.dateStr) {
        DateUtils.formatReadableDateArabic(daySummary.dateStr)
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
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${habit.title} ${habit.iconEmoji}",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = readableDate,
                            color = StreqaTeal,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Status Selector
                Text(text = "حالة اليوم", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Done
                    val isDone = selectedStatus == HabitStatus.DONE
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDone) StreqaTeal else Color(0xFF192229))
                            .border(1.dp, if (isDone) StreqaTeal else StreqaCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedStatus = HabitStatus.DONE }
                            .padding(vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isDone) Color(0xFF042F2E) else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "تم الإنجاز",
                                color = if (isDone) Color(0xFF042F2E) else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Failed
                    val isFailed = selectedStatus == HabitStatus.FAILED
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isFailed) Color(0xFFEF4444) else Color(0xFF192229))
                            .border(1.dp, if (isFailed) Color(0xFFEF4444) else StreqaCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedStatus = HabitStatus.FAILED }
                            .padding(vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = if (isFailed) Color.White else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "إخفاق",
                                color = if (isFailed) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // None / Reset
                    val isNone = selectedStatus == HabitStatus.NONE
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isNone) Color(0xFF334155) else Color(0xFF192229))
                            .border(1.dp, if (isNone) Color(0xFF64748B) else StreqaCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedStatus = HabitStatus.NONE }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "مسودة",
                            color = if (isNone) Color.White else TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Notes text field
                Text(
                    text = "ملاحظات اليوم (سبب النجاح أو الإخفاق)",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = {
                        Text(
                            text = "اكتب سبباً للنجاح، شعورك اليوم، أو ما منعك من الإنجاز...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
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
                        .testTag("day_note_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick chips
                Text(text = "أسباب سريعة مقترحة:", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val reasons = if (selectedStatus == HabitStatus.FAILED) quickFailureReasons else quickSuccessReasons
                    reasons.forEach { reason ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF222C35))
                                .border(1.dp, StreqaCardBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    notes = if (notes.isBlank()) reason else "$notes - $reason"
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = reason, color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Save button
                Button(
                    onClick = {
                        onSave(selectedStatus, notes.trim())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StreqaTeal),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_day_note_btn")
                ) {
                    Text(
                        text = "حفظ الملاحظة والحالة",
                        color = Color(0xFF042F2E),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
