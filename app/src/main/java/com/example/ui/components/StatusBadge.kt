package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FeeStatus
import com.example.ui.theme.StatusPaidBg
import com.example.ui.theme.StatusPaidBorder
import com.example.ui.theme.StatusPaidText
import com.example.ui.theme.StatusPartialBg
import com.example.ui.theme.StatusPartialBorder
import com.example.ui.theme.StatusPartialText
import com.example.ui.theme.StatusUnpaidBg
import com.example.ui.theme.StatusUnpaidBorder
import com.example.ui.theme.StatusUnpaidText

@Composable
fun StatusBadge(
    status: FeeStatus,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val (bgColor, textColor, borderColor, icon, label) = when (status) {
        FeeStatus.PAID -> StatusInfo(
            StatusPaidBg,
            StatusPaidText,
            StatusPaidBorder,
            Icons.Default.CheckCircle,
            "PAID"
        )
        FeeStatus.PARTIAL -> StatusInfo(
            StatusPartialBg,
            StatusPartialText,
            StatusPartialBorder,
            Icons.Default.HourglassTop,
            "PARTIAL"
        )
        FeeStatus.UNPAID -> StatusInfo(
            StatusUnpaidBg,
            StatusUnpaidText,
            StatusUnpaidBorder,
            Icons.Default.Warning,
            "UNPAID"
        )
    }

    Row(
        modifier = modifier
            .testTag("status_badge_${label.lowercase()}")
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 3.dp else 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(if (compact) 11.dp else 13.dp)
        )
        Text(
            text = label,
            color = textColor,
            fontSize = if (compact) 10.sp else 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}

private data class StatusInfo(
    val bg: Color,
    val text: Color,
    val border: Color,
    val icon: ImageVector,
    val label: String
)

