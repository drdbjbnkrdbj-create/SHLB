package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DaySummary
import com.example.data.model.HabitStatus
import com.example.ui.theme.StreqaDarkBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun DayStatusCircle(
    daySummary: DaySummary,
    themeColor: Color,
    onToggle: () -> Unit,
    onOpenNote: () -> Unit,
    dayLabelFontSize: TextUnit = 10.sp,
    modifier: Modifier = Modifier
) {
    val isDone = daySummary.status == HabitStatus.DONE
    val isFailed = daySummary.status == HabitStatus.FAILED

    val bgColor = when {
        isDone -> themeColor
        isFailed -> Color(0xFFEF4444)
        daySummary.isToday -> Color(0xFF222C35)
        else -> Color(0xFF161E24)
    }

    val borderColor = when {
        isDone -> themeColor
        isFailed -> Color(0xFFDC2626)
        daySummary.isToday -> themeColor.copy(alpha = 0.7f)
        daySummary.isFuture -> Color(0xFF242F38)
        else -> Color(0xFF2D3B47)
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.testTag("day_circle_${daySummary.dateStr}")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(
                    width = if (daySummary.isToday && !isDone && !isFailed) 1.5.dp else 1.dp,
                    color = borderColor,
                    shape = CircleShape
                )
                .clickable(onClick = onToggle)
        ) {
            when {
                isDone -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "تم الإنجاز",
                        tint = if (themeColor.luminance() > 0.5f) Color(0xFF0F172A) else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                isFailed -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إخفاق",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                daySummary.isToday -> {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(themeColor)
                    )
                }
            }

            // Note badge - clickable to view/edit note directly
            if (daySummary.note.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFBBF24))
                        .border(1.5.dp, StreqaDarkBg, CircleShape)
                        .clickable { onOpenNote() }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = daySummary.dayOfWeekNameArabic,
            color = if (daySummary.isToday) themeColor else if (daySummary.isFuture) TextMuted else TextSecondary,
            fontSize = dayLabelFontSize,
            fontWeight = if (daySummary.isToday) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}
