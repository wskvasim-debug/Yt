package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FeeStatus
import com.example.model.Student
import com.example.ui.theme.*
import com.example.util.Formatters

@Composable
fun StudentFeeCard(
    student: Student,
    onSavePayment: (Student, Double, Double, Double) -> Unit,
    onEditDetails: (Student) -> Unit,
    onViewReceipt: (Student) -> Unit,
    onDeleteStudent: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local draft states for direct table/field edits
    var totalFeesText by remember(student.id, student.totalFees) {
        mutableStateOf(student.totalFees.toInt().toString())
    }
    var firstPaidText by remember(student.id, student.firstInstallmentPaid) {
        mutableStateOf(student.firstInstallmentPaid.toInt().toString())
    }
    var secondPaidText by remember(student.id, student.secondInstallmentPaid) {
        mutableStateOf(student.secondInstallmentPaid.toInt().toString())
    }

    var isExpanded by remember { mutableStateOf(false) }

    // Parsed numeric values
    val currentTotalFees = remember(totalFeesText) { Formatters.parseAmount(totalFeesText) }
    val currentFirstPaid = remember(firstPaidText) { Formatters.parseAmount(firstPaidText) }
    val currentSecondPaid = remember(secondPaidText) { Formatters.parseAmount(secondPaidText) }

    // Live calculated derived values
    val currentTotalPaid = remember(currentFirstPaid, currentSecondPaid) {
        currentFirstPaid + currentSecondPaid
    }
    val currentPendingFees = remember(currentTotalFees, currentTotalPaid) {
        (currentTotalFees - currentTotalPaid).coerceAtLeast(0.0)
    }
    val currentStatus = remember(currentPendingFees, currentTotalPaid) {
        when {
            currentPendingFees <= 0.0 -> FeeStatus.PAID
            currentTotalPaid > 0.0 -> FeeStatus.PARTIAL
            else -> FeeStatus.UNPAID
        }
    }

    // Has changes check
    val hasUnsavedChanges = remember(
        student,
        currentTotalFees,
        currentFirstPaid,
        currentSecondPaid
    ) {
        student.totalFees != currentTotalFees ||
        student.firstInstallmentPaid != currentFirstPaid ||
        student.secondInstallmentPaid != currentSecondPaid
    }

    val cardBorderColor by animateColorAsState(
        targetValue = if (hasUnsavedChanges) SchoolPrimary else Slate200,
        label = "cardBorderColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("student_card_${student.rollNo.lowercase()}")
            .border(
                width = if (hasUnsavedChanges) 1.5.dp else 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1: Roll No, Name, Class & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Roll No Pill (Geometric Balance Avatar)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier
                            .testTag("roll_no_${student.rollNo.lowercase()}")
                            .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            text = student.rollNo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SchoolPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Column {
                        Text(
                            text = student.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            maxLines = 1
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = student.studentClass,
                                fontSize = 11.sp,
                                color = SchoolPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (student.parentContact.isNotBlank()) {
                                Text(text = "•", fontSize = 10.sp, color = Slate400)
                                Text(
                                    text = student.parentContact,
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }
                        }
                    }
                }

                // Dynamic Status Badge
                StatusBadge(status = currentStatus)
            }

            HorizontalDivider(color = Slate100, thickness = 1.dp)

            // Table / Input Fields Grid: Total Fees, 1st Paid, 2nd Paid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total Fees Input
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Fees",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = totalFeesText,
                        onValueChange = { totalFeesText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        prefix = { Text("₹", fontSize = 12.sp, color = Slate600) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_total_fees_${student.rollNo.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SchoolPrimary,
                            unfocusedBorderColor = Slate200,
                            unfocusedContainerColor = Slate50
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // 1st Installment Input
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "1st Paid",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = firstPaidText,
                        onValueChange = { firstPaidText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        prefix = { Text("₹", fontSize = 12.sp, color = Slate600) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_1st_paid_${student.rollNo.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SchoolPrimary,
                            unfocusedBorderColor = Slate200,
                            unfocusedContainerColor = Slate50
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentFirstPaid > 0) Color(0xFF15803D) else Slate900
                        )
                    )
                }

                // 2nd Installment Input
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "2nd Paid",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = secondPaidText,
                        onValueChange = { secondPaidText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        prefix = { Text("₹", fontSize = 12.sp, color = Slate600) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_2nd_paid_${student.rollNo.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SchoolPrimary,
                            unfocusedBorderColor = Slate200,
                            unfocusedContainerColor = Slate50
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentSecondPaid > 0) Color(0xFF15803D) else Slate900
                        )
                    )
                }
            }

            // Calculation Summary Bar: Total Paid & Auto-calculated Pending Fees
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Slate50,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Total Collected for this student
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Total Paid:",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                        Text(
                            text = Formatters.formatCurrency(currentTotalPaid),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }

                    // Auto-calculated Pending Fees
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Pending:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate600
                        )
                        Text(
                            text = Formatters.formatCurrency(currentPendingFees),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (currentPendingFees > 0) Color(0xFFBE123C) else Color(0xFF15803D),
                            modifier = Modifier.testTag("pending_fee_${student.rollNo.lowercase()}")
                        )
                    }
                }
            }

            // Quick Payment Helper Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val halfFee = (currentTotalFees / 2).toInt()

                QuickActionPill(
                    label = "Pay 1st (₹$halfFee)",
                    onClick = {
                        firstPaidText = halfFee.toString()
                    }
                )

                QuickActionPill(
                    label = "Pay 2nd (₹$halfFee)",
                    onClick = {
                        secondPaidText = halfFee.toString()
                    }
                )

                QuickActionPill(
                    label = "Clear",
                    onClick = {
                        firstPaidText = "0"
                        secondPaidText = "0"
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Toggle more actions (receipt, notes, edit full)
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand student details",
                        tint = Slate600,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded details & remarks
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (student.remarks.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Remarks: ${student.remarks}",
                                fontSize = 12.sp,
                                color = SchoolPrimary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onViewReceipt(student) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_receipt_${student.rollNo.lowercase()}"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "Fee Receipt",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Receipt", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { onEditDetails(student) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_edit_${student.rollNo.lowercase()}"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit student",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = { onDeleteStudent(student) },
                            modifier = Modifier.testTag("btn_delete_${student.rollNo.lowercase()}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            // Primary Action: "Save / Update" Button
            Button(
                onClick = {
                    onSavePayment(
                        student,
                        currentFirstPaid,
                        currentSecondPaid,
                        currentTotalFees
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_save_${student.rollNo.lowercase()}"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasUnsavedChanges) SchoolPrimary else Slate800
                )
            ) {
                Icon(
                    imageVector = if (hasUnsavedChanges) Icons.Default.Save else Icons.Default.Check,
                    contentDescription = "Save fee record",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (hasUnsavedChanges) "Save / Update Record *" else "Record Saved",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun QuickActionPill(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Slate100,
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(1.dp, Slate200, RoundedCornerShape(6.dp))
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate700,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}
