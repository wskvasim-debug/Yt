package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Class
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.Formatters
import com.example.viewmodel.ClassSummary
import com.example.viewmodel.DashboardStats

@Composable
fun DashboardOverview(
    stats: DashboardStats,
    selectedClass: String,
    onSelectClass: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Geometric Balance 3-Card Summary Grid (Expected, Collected, Pending)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Expected
            GeometricMetricCard(
                label = "Expected",
                amount = stats.totalExpectedFees,
                percentageText = "Total 100%",
                containerColor = GeoExpectedBg,
                borderColor = GeoExpectedBorder,
                textColor = GeoExpectedText,
                modifier = Modifier.weight(1f),
                testTag = "metric_total_expected"
            )

            // Collected
            val collectedPct = (stats.collectionRate * 100).toInt()
            GeometricMetricCard(
                label = "Collected",
                amount = stats.totalCollectedFees,
                percentageText = "Total $collectedPct%",
                containerColor = GeoCollectedBg,
                borderColor = GeoCollectedBorder,
                textColor = GeoCollectedText,
                modifier = Modifier.weight(1f),
                testTag = "metric_total_collected"
            )

            // Pending
            val pendingPct = ((1f - stats.collectionRate).coerceAtLeast(0f) * 100).toInt()
            GeometricMetricCard(
                label = "Pending",
                amount = stats.totalPendingFees,
                percentageText = "Total $pendingPct%",
                containerColor = GeoPendingBg,
                borderColor = GeoPendingBorder,
                textColor = GeoPendingText,
                modifier = Modifier.weight(1f),
                testTag = "metric_total_pending"
            )
        }

        // Class-Wise Summary Cards Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Class,
                        contentDescription = "Classes",
                        tint = SchoolPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Class-wise Overview",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }
                Text(
                    text = "Tap to filter",
                    fontSize = 11.sp,
                    color = Slate500
                )
            }

            // Horizontal scrolling row of class-wise cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(stats.classSummaries, key = { it.className }) { classSummary ->
                    ClassSummaryCard(
                        summary = classSummary,
                        isSelected = selectedClass.equals(classSummary.className, ignoreCase = true),
                        onClick = { onSelectClass(classSummary.className) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GeometricMetricCard(
    label: String,
    amount: Double,
    percentageText: String,
    containerColor: Color,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .height(98.dp)
            .border(1.dp, borderColor, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.75f),
                letterSpacing = 0.5.sp
            )

            Column {
                Text(
                    text = Formatters.formatCurrency(amount),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    maxLines = 1
                )
                Text(
                    text = percentageText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.65f)
                )
            }
        }
    }
}

@Composable
fun ClassSummaryCard(
    summary: ClassSummary,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SchoolPrimary else Slate200
    val containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White

    Card(
        modifier = Modifier
            .width(170.dp)
            .testTag("class_card_${summary.className.lowercase().replace(" ", "_")}")
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Class Title and Student count badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = summary.className,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isSelected) SchoolPrimary else Slate900
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) SchoolPrimary else Slate100
                ) {
                    Text(
                        text = "${summary.studentCount}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Slate600,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Collected vs Pending
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Collected:", fontSize = 10.sp, color = Slate500)
                    Text(
                        text = Formatters.formatCurrency(summary.totalCollected),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Pending:", fontSize = 10.sp, color = Slate500)
                    Text(
                        text = Formatters.formatCurrency(summary.totalPending),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (summary.totalPending > 0) Color(0xFFBE123C) else Color(0xFF15803D)
                    )
                }
            }

            // Progress bar
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                LinearProgressIndicator(
                    progress = { summary.collectionPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = if (summary.collectionPercentage >= 1f) Color(0xFF15803D) else SchoolPrimary,
                    trackColor = Slate200
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(summary.collectionPercentage * 100).toInt()}% collected",
                        fontSize = 9.sp,
                        color = Slate500
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "View class",
                        tint = if (isSelected) SchoolPrimary else Slate400,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}

