package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.model.Student
import com.example.ui.components.AddStudentDialog
import com.example.ui.components.ClassFilterTabs
import com.example.ui.components.DashboardOverview
import com.example.ui.components.EditStudentDialog
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.StudentFeeCard
import com.example.ui.theme.*
import com.example.viewmodel.FeeViewModel
import com.example.viewmodel.SortOption
import com.example.viewmodel.StatusFilter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IlmaSchoolApp(
    viewModel: FeeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val displayedStudents by viewModel.displayedStudents.collectAsStateWithLifecycle()
    val selectedClass by viewModel.selectedClass.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
    val currentSort by viewModel.sortOption.collectAsStateWithLifecycle()

    val isAddOpen by viewModel.isAddDialogOpen.collectAsStateWithLifecycle()
    val editingStudent by viewModel.editingStudent.collectAsStateWithLifecycle()
    val receiptStudent by viewModel.receiptStudent.collectAsStateWithLifecycle()
    val studentToDelete by viewModel.studentToDelete.collectAsStateWithLifecycle()
    val isResetOpen by viewModel.isResetDialogOpen.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isSortMenuOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Compute class count map for tab badges
    val classCounts = remember(stats.classSummaries, stats.totalStudents) {
        val map = mutableMapOf<String, Int>()
        map["All Classes"] = stats.totalStudents
        stats.classSummaries.forEach {
            map[it.className] = it.studentCount
        }
        map
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Slate50,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate100)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Geometric IS badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SchoolPrimary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "IS",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Ilma School",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                text = "FEE MANAGEMENT SYSTEM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate500,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset Demo Data button
                        IconButton(
                            onClick = { viewModel.openResetDialog() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_reset_demo_data")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset Demo Data",
                                tint = Slate600,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Add Student Button
                        Surface(
                            shape = CircleShape,
                            color = Slate100,
                            modifier = Modifier
                                .size(36.dp)
                                .border(1.dp, Slate200, CircleShape)
                                .clickable { viewModel.openAddDialog() }
                                .testTag("topbar_btn_add_student")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Student",
                                    tint = SchoolPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = SchoolPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_student")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Student",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Add Student",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Dashboard Overview Section
            item {
                Spacer(modifier = Modifier.height(2.dp))
                DashboardOverview(
                    stats = stats,
                    selectedClass = selectedClass,
                    onSelectClass = { className ->
                        viewModel.selectClass(className)
                    }
                )
            }

            // 2. Class Navigation Tabs / Dropdown
            item {
                ClassFilterTabs(
                    classes = viewModel.availableClasses,
                    selectedClass = selectedClass,
                    onClassSelected = { viewModel.selectClass(it) },
                    classCounts = classCounts
                )
            }

            // 3. Search Bar & Status Filter Chips
            item {
                SearchAndFilterSection(
                    selectedClass = selectedClass,
                    studentCount = displayedStudents.size,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    selectedStatus = selectedStatusFilter,
                    onStatusSelected = { viewModel.setStatusFilter(it) },
                    currentSort = currentSort,
                    onSortSelected = { viewModel.setSortOption(it) },
                    isSortMenuOpen = isSortMenuOpen,
                    onToggleSortMenu = { isSortMenuOpen = it }
                )
            }

            // 4. Student Fee Table & Field Cards
            if (displayedStudents.isEmpty()) {
                item {
                    EmptyStudentsView(
                        searchQuery = searchQuery,
                        selectedClass = selectedClass,
                        onClearSearch = {
                            viewModel.setSearchQuery("")
                            viewModel.setStatusFilter(StatusFilter.ALL)
                        },
                        onAddStudent = { viewModel.openAddDialog() }
                    )
                }
            } else {
                items(displayedStudents, key = { it.id }) { student ->
                    StudentFeeCard(
                        student = student,
                        onSavePayment = { targetStudent, firstPaid, secondPaid, totalFees ->
                            viewModel.updateStudentPayments(
                                student = targetStudent,
                                firstPaid = firstPaid,
                                secondPaid = secondPaid,
                                totalFees = totalFees
                            )
                        },
                        onEditDetails = { viewModel.openEditDialog(it) },
                        onViewReceipt = { viewModel.openReceiptDialog(it) },
                        onDeleteStudent = { viewModel.promptDelete(it) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }

    // Add Student Dialog
    if (isAddOpen) {
        AddStudentDialog(
            initialClass = selectedClass,
            onDismiss = { viewModel.closeAddDialog() },
            onConfirmAdd = { rollNo, name, studentClass, totalFees, firstPaid, secondPaid, phone, remarks ->
                viewModel.addStudent(
                    rollNo = rollNo,
                    name = name,
                    studentClass = studentClass,
                    totalFees = totalFees,
                    firstPaid = firstPaid,
                    secondPaid = secondPaid,
                    phone = phone,
                    remarks = remarks
                )
            }
        )
    }

    // Edit Student Dialog
    editingStudent?.let { student ->
        EditStudentDialog(
            student = student,
            onDismiss = { viewModel.closeEditDialog() },
            onConfirmSave = { id, rollNo, name, studentClass, totalFees, firstPaid, secondPaid, phone, remarks ->
                viewModel.saveEditedStudent(
                    id = id,
                    rollNo = rollNo,
                    name = name,
                    studentClass = studentClass,
                    totalFees = totalFees,
                    firstPaid = firstPaid,
                    secondPaid = secondPaid,
                    phone = phone,
                    remarks = remarks
                )
            }
        )
    }

    // Fee Receipt Dialog
    receiptStudent?.let { student ->
        ReceiptDialog(
            student = student,
            onDismiss = { viewModel.closeReceiptDialog() },
            onShare = {
                scope.launch {
                    snackbarHostState.showSnackbar("Fee receipt for ${student.name} prepared")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    studentToDelete?.let { student ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelDelete() },
            title = { Text("Delete Student Record", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to remove ${student.name} (${student.rollNo}) from ${student.studentClass}?")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDeleteStudent() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.cancelDelete() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Demo Data Confirmation Dialog
    if (isResetOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.closeResetDialog() },
            title = { Text("Reset to Default Demo Data", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will restore the original 20 student sample records across Nursery, Jr. KG, Sr. KG, 1st Standard, and 2nd Standard. Any custom changes will be reset.")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetToDemoData() },
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary)
                ) {
                    Text("Reset Database")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.closeResetDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SchoolBannerCard(
    totalStudents: Int,
    academicYear: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.school_banner_1787934826120),
                contentDescription = "School campus",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )

            Surface(
                color = SchoolNavy.copy(alpha = 0.78f),
                modifier = Modifier
                    .matchParentSize()
            ) {}

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF59E0B)
                    ) {
                        Text(
                            text = "ACADEMIC SESSION $academicYear",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SchoolNavy,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ilma School Fee Portal",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Real-time fee installments, status badges & collection tracking",
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    selectedClass: String,
    studentCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatus: StatusFilter,
    onStatusSelected: (StatusFilter) -> Unit,
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    isSortMenuOpen: Boolean,
    onToggleSortMenu: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Title & Count Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (selectedClass == "All Classes") "All Students" else "$selectedClass Students",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Slate100
                ) {
                    Text(
                        text = "$studentCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate600,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            // Current sort indicator
            Text(
                text = "Sort: ${currentSort.displayName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Slate500
            )
        }

        // Search bar & Sort trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search by name, roll no. or phone...", fontSize = 12.sp, color = Slate400) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = Slate600,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_students_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SchoolPrimary,
                    unfocusedBorderColor = Slate200,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            // Sort Menu Button
            Box {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    modifier = Modifier
                        .border(1.dp, Slate200, RoundedCornerShape(14.dp))
                        .clickable { onToggleSortMenu(true) }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = SchoolPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = isSortMenuOpen,
                    onDismissRequest = { onToggleSortMenu(false) }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.displayName,
                                    fontWeight = if (currentSort == option) FontWeight.Bold else FontWeight.Normal,
                                    color = if (currentSort == option) SchoolPrimary else Slate800
                                )
                            },
                            onClick = {
                                onSortSelected(option)
                                onToggleSortMenu(false)
                            }
                        )
                    }
                }
            }
        }

        // Status Filter Chips Row (All, Paid, Partial, Unpaid)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Status:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate500
            )

            StatusFilter.values().forEach { status ->
                val isSelected = selectedStatus == status
                FilterChip(
                    selected = isSelected,
                    onClick = { onStatusSelected(status) },
                    label = {
                        Text(
                            text = when (status) {
                                StatusFilter.ALL -> "All"
                                StatusFilter.PAID -> "Paid"
                                StatusFilter.PARTIAL -> "Partial"
                                StatusFilter.UNPAID -> "Unpaid"
                            },
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (status) {
                            StatusFilter.ALL -> SchoolPrimary
                            StatusFilter.PAID -> Color(0xFF15803D)
                            StatusFilter.PARTIAL -> Color(0xFFB45309)
                            StatusFilter.UNPAID -> Color(0xFFBE123C)
                        },
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Slate600
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) Color.Transparent else Slate200
                    ),
                    modifier = Modifier.testTag("filter_chip_${status.name.lowercase()}")
                )
            }
        }
    }
}

@Composable
private fun EmptyStudentsView(
    searchQuery: String,
    selectedClass: String,
    onClearSearch: () -> Unit,
    onAddStudent: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Slate100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Slate400,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "No Students Found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Text(
                text = if (searchQuery.isNotBlank()) {
                    "No students match \"$searchQuery\" in $selectedClass."
                } else {
                    "There are no student fee records for $selectedClass."
                },
                fontSize = 12.sp,
                color = Slate600,
                textAlign = TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (searchQuery.isNotBlank()) {
                    OutlinedButton(onClick = onClearSearch) {
                        Text("Clear Filters")
                    }
                }
                Button(
                    onClick = onAddStudent,
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Student")
                }
            }
        }
    }
}
