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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
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
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SchoolPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.util.Formatters

@Composable
fun AddStudentDialog(
    initialClass: String,
    onDismiss: () -> Unit,
    onConfirmAdd: (
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
    val defaultClass = if (initialClass != "All Classes") initialClass else "Nursery"

    var selectedClass by remember { mutableStateOf(defaultClass) }
    var classDropdownExpanded by remember { mutableStateOf(false) }

    var rollNo by remember {
        mutableStateOf(
            when (defaultClass) {
                "Nursery" -> "NUR-" + (105..199).random()
                "Jr. KG" -> "JKG-" + (205..299).random()
                "Sr. KG" -> "SKG-" + (305..399).random()
                "1st Standard" -> "1ST-" + (405..499).random()
                "2nd Standard" -> "2ND-" + (505..599).random()
                else -> "ILM-" + (600..999).random()
            }
        )
    }

    var name by remember { mutableStateOf("") }
    var totalFeesText by remember {
        mutableStateOf(
            when (defaultClass) {
                "Nursery" -> "16000"
                "Jr. KG" -> "18000"
                "Sr. KG" -> "20000"
                "1st Standard" -> "22000"
                "2nd Standard" -> "24000"
                else -> "20000"
            }
        )
    }
    var firstPaidText by remember { mutableStateOf("0") }
    var secondPaidText by remember { mutableStateOf("0") }
    var parentContact by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

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
                .testTag("add_student_dialog"),
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
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = SchoolPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Add New Student",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "Ilma School Fee Registration",
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

                // Class Dropdown Selector
                Column {
                    Text(
                        text = "Assign Class *",
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
                                        // Suggest default fees for this class
                                        totalFeesText = when (c) {
                                            "Nursery" -> "16000"
                                            "Jr. KG" -> "18000"
                                            "Sr. KG" -> "20000"
                                            "1st Standard" -> "22000"
                                            "2nd Standard" -> "24000"
                                            else -> "20000"
                                        }
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
                            text = "Roll No. / ID *",
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_add_roll_no"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SchoolPrimary,
                                unfocusedBorderColor = Slate200
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(0.55f)) {
                        Text(
                            text = "Student Name *",
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
                            placeholder = { Text("e.g. Zaid Khan", fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_add_name"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SchoolPrimary,
                                unfocusedBorderColor = Slate200
                            )
                        )
                    }
                }

                // Fee Inputs (Total Fees, 1st Paid, 2nd Paid)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Total Fees *",
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_add_total_fees"),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_add_1st_paid"),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_add_2nd_paid"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SchoolPrimary,
                                unfocusedBorderColor = Slate200
                            )
                        )
                    }
                }

                // Live Fee Status Preview Card
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
                                text = "Auto Calculated Balance:",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                            Text(
                                text = "Pending: ${Formatters.formatCurrency(pending)}",
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
                        placeholder = { Text("Parent Phone (e.g. +91 98765 43210)", fontSize = 12.sp) },
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
                        placeholder = { Text("e.g. Paid cash at reception / Fee concession", fontSize = 12.sp) },
                        label = { Text("Notes / Remarks (Optional)", fontSize = 12.sp) },
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
                                errorMessage = "Please enter student name."
                                return@Button
                            }
                            if (rollNo.isBlank()) {
                                errorMessage = "Please enter roll number."
                                return@Button
                            }
                            onConfirmAdd(
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
                            .testTag("btn_confirm_add_student"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Student", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
