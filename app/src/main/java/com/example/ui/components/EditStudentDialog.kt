package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.DummyData
import com.example.model.FeeStatus
import com.example.model.Student
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SchoolPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.util.Formatters

@Composable
fun EditStudentDialog(
    student: Student,
    onDismiss: () -> Unit,
    onConfirmSave: (
        id: Long,
        rollNo: String,
        name: String,
        studentClass: String,
        totalFees: Double,
        firstPaid: Double,
        secondPaid: Double,
        phone: String,
        remarks: String
    ) -> Unit
) {
    var selectedClass by remember { mutableStateOf(student.studentClass) }
    var classDropdownExpanded by remember { mutableStateOf(false) }

    var rollNo by remember { mutableStateOf(student.rollNo) }
    var name by remember { mutableStateOf(student.name) }
    var totalFeesText by remember { mutableStateOf(student.totalFees.toInt().toString()) }
    var firstPaidText by remember { mutableStateOf(student.firstInstallmentPaid.toInt().toString()) }
    var secondPaidText by remember { mutableStateOf(student.secondInstallmentPaid.toInt().toString()) }
    var parentContact by remember { mutableStateOf(student.parentContact) }
    var remarks by remember { mutableStateOf(student.remarks) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val totalFees = remember(totalFeesText) { Formatters.parseAmount(totalFeesText) }
    val firstPaid = remember(firstPaidText) { Formatters.parseAmount(firstPaidText) }
    val secondPaid = remember(secondPaidText) { Formatters.parseAmount(secondPaidText) }
    val totalPaid = firstPaid + secondPaid
    val pending = (totalFees - totalPaid).coerceAtLeast(0.0)

    val previewStatus = when {
        pending <= 0.0 && totalFees > 0 -> FeeStatus.PAID
        totalPaid > 0.0 -> FeeStatus.PARTIAL
        else -> FeeStatus.UNPAID
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("edit_student_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SchoolPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = SchoolPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Edit Student Record",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Update details & fee installments",
                                fontSize = 12.sp,
                                color = Slate600
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Slate600
                        )
                    }
                }

                HorizontalDivider(color = Slate100)

                // Class Dropdown
                Column {
                    Text(
                        text = "Class",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                                .clickable { classDropdownExpanded = true }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            color = Slate100
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedClass,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SchoolNavy
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select class"
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = classDropdownExpanded,
                            onDismissRequest = { classDropdownExpanded = false }
                        ) {
                            DummyData.classes.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        selectedClass = c
                                        classDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Roll No & Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(0.45f)) {
                        Text(
                            text = "Roll No.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = rollNo,
                            onValueChange = { rollNo = it },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SchoolPrimary,
                                unfocusedBorderColor = Slate200
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(0.55f)) {
                        Text(
                            text = "Student Name",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                errorMessage = null
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SchoolPrimary,
                                unfocusedBorderColor = Slate200
                            )
                        )
                    }
                }

                // Installments & Fees
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                            prefix = { Text("₹", fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SchoolPrimary,
                                unfocusedBorderColor = Slate200
                            )
                        )
                    }

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
                            prefix = { Text("₹", fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SchoolPrimary,
                                unfocusedBorderColor = Slate200
                            )
                        )
                    }

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
                            prefix = { Text("₹", fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SchoolPrimary,
                                unfocusedBorderColor = Slate200
                            )
                        )
                    }
                }

                // Balance preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pending Fee Balance:",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                            Text(
                                text = Formatters.formatCurrency(pending),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pending > 0) Color(0xFFDC2626) else Color(0xFF059669)
                            )
                        }

                        StatusBadge(status = previewStatus)
                    }
                }

                // Contact & Remarks
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = parentContact,
                        onValueChange = { parentContact = it },
                        singleLine = true,
                        label = { Text("Parent Contact Number", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SchoolPrimary,
                            unfocusedBorderColor = Slate200
                        )
                    )

                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        singleLine = true,
                        label = { Text("Remarks", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SchoolPrimary,
                            unfocusedBorderColor = Slate200
                        )
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = Slate700)
                    }

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Name cannot be empty"
                                return@Button
                            }
                            onConfirmSave(
                                student.id,
                                rollNo,
                                name,
                                selectedClass,
                                totalFees,
                                firstPaid,
                                secondPaid,
                                parentContact,
                                remarks
                            )
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("btn_confirm_edit_student"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Update Student", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
