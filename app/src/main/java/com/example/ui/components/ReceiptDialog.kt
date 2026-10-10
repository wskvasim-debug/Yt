package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Student
import com.example.ui.theme.*
import com.example.util.Formatters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptDialog(
    student: Student,
    onDismiss: () -> Unit,
    onShare: () -> Unit = {}
) {
    val receiptNo = "REC-ILM-" + (1000 + student.id * 37)
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(student.updatedAt))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("receipt_dialog")
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FEE PAYMENT RECEIPT",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SchoolPrimary,
                        letterSpacing = 1.sp
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Slate600
                        )
                    }
                }

                // Official School Header inside Receipt
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SchoolNavy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Ilma School",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "ILMA SCHOOL",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Excellence in Education • Academic Year 2026-2027",
                            fontSize = 11.sp,
                            color = Color(0xFF93C5FD)
                        )
                    }
                }

                // Receipt Meta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Receipt No:", fontSize = 11.sp, color = Slate600)
                        Text(
                            text = receiptNo,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Date & Time:", fontSize = 11.sp, color = Slate600)
                        Text(
                            text = dateStr,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate800
                        )
                    }
                }

                HorizontalDivider(color = Slate200)

                // Student Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate100)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ReceiptRow(label = "Student Name", value = student.name, isBold = true)
                    ReceiptRow(label = "Roll No.", value = student.rollNo, isBold = true)
                    ReceiptRow(label = "Class", value = student.studentClass, isBold = false)
                    if (student.parentContact.isNotBlank()) {
                        ReceiptRow(label = "Contact", value = student.parentContact, isBold = false)
                    }
                }

                // Fee Breakdown Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Payment Breakdown",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                    HorizontalDivider(color = Slate200)

                    ReceiptRow(
                        label = "Total Annual Fees",
                        value = Formatters.formatCurrency(student.totalFees),
                        isBold = false
                    )
                    ReceiptRow(
                        label = "1st Installment Paid",
                        value = Formatters.formatCurrency(student.firstInstallmentPaid),
                        isBold = false,
                        valueColor = Color(0xFF059669)
                    )
                    ReceiptRow(
                        label = "2nd Installment Paid",
                        value = Formatters.formatCurrency(student.secondInstallmentPaid),
                        isBold = false,
                        valueColor = Color(0xFF059669)
                    )

                    HorizontalDivider(color = Slate200)

                    ReceiptRow(
                        label = "Total Amount Paid",
                        value = Formatters.formatCurrency(student.totalPaid),
                        isBold = true,
                        valueColor = Color(0xFF059669)
                    )
                    ReceiptRow(
                        label = "Pending Balance",
                        value = Formatters.formatCurrency(student.pendingFees),
                        isBold = true,
                        valueColor = if (student.pendingFees > 0) Color(0xFFDC2626) else Color(0xFF059669)
                    )
                }

                // Status & Stamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Payment Status:", fontSize = 11.sp, color = Slate600)
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusBadge(status = student.feeStatus)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "AUTHORIZED SIGNATORY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate600,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Ilma Accounts Office",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SchoolNavy
                        )
                    }
                }

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close", color = Slate700)
                    }

                    Button(
                        onClick = {
                            onShare()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Receipt")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = Slate900
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isBold) Slate900 else Slate600,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}
