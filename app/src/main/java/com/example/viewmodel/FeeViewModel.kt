package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DummyData
import com.example.data.StudentRepository
import com.example.model.FeeStatus
import com.example.model.Student
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ClassSummary(
    val className: String,
    val studentCount: Int,
    val totalExpected: Double,
    val totalCollected: Double,
    val totalPending: Double,
    val collectionPercentage: Float
)

data class DashboardStats(
    val totalExpectedFees: Double = 0.0,
    val totalCollectedFees: Double = 0.0,
    val totalPendingFees: Double = 0.0,
    val totalStudents: Int = 0,
    val paidStudentsCount: Int = 0,
    val partialStudentsCount: Int = 0,
    val unpaidStudentsCount: Int = 0,
    val collectionRate: Float = 0f,
    val classSummaries: List<ClassSummary> = emptyList()
)

enum class StatusFilter {
    ALL,
    PAID,
    PARTIAL,
    UNPAID
}

enum class SortOption(val displayName: String) {
    ROLL_NO_ASC("Roll No (A-Z)"),
    NAME_ASC("Name (A-Z)"),
    PENDING_DESC("Pending Fees (Highest)"),
    TOTAL_FEES_DESC("Total Fees (Highest)")
}

class FeeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository
    val availableClasses = listOf("All Classes") + DummyData.classes

    private val _selectedClass = MutableStateFlow("All Classes")
    val selectedClass: StateFlow<String> = _selectedClass.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow(StatusFilter.ALL)
    val selectedStatusFilter: StateFlow<StatusFilter> = _selectedStatusFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.ROLL_NO_ASC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Dialog States
    private val _isAddDialogOpen = MutableStateFlow(false)
    val isAddDialogOpen: StateFlow<Boolean> = _isAddDialogOpen.asStateFlow()

    private val _editingStudent = MutableStateFlow<Student?>(null)
    val editingStudent: StateFlow<Student?> = _editingStudent.asStateFlow()

    private val _receiptStudent = MutableStateFlow<Student?>(null)
    val receiptStudent: StateFlow<Student?> = _receiptStudent.asStateFlow()

    private val _studentToDelete = MutableStateFlow<Student?>(null)
    val studentToDelete: StateFlow<Student?> = _studentToDelete.asStateFlow()

    private val _isResetDialogOpen = MutableStateFlow(false)
    val isResetDialogOpen: StateFlow<Boolean> = _isResetDialogOpen.asStateFlow()

    private val allStudentsFlow: StateFlow<List<Student>>

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = StudentRepository(database.studentDao())

        viewModelScope.launch {
            repository.ensureDataInitialized()
        }

        allStudentsFlow = repository.allStudents.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // High-level summary dashboard calculation
    val dashboardStats: StateFlow<DashboardStats> = allStudentsFlow
        .combine(_selectedClass) { students, _ ->
            computeDashboardStats(students)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardStats()
        )

    // Filtered & sorted student list for table
    val displayedStudents: StateFlow<List<Student>> = combine(
        allStudentsFlow,
        _selectedClass,
        _searchQuery,
        _selectedStatusFilter,
        _sortOption
    ) { students, selectedClass, query, statusFilter, sort ->
        var list = students

        // 1. Filter by Class
        if (selectedClass != "All Classes") {
            list = list.filter { it.studentClass.equals(selectedClass, ignoreCase = true) }
        }

        // 2. Filter by Search Query (Name or Roll No)
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.rollNo.lowercase().contains(q) ||
                it.parentContact.lowercase().contains(q)
            }
        }

        // 3. Filter by Fee Status (Paid, Partial, Unpaid)
        list = when (statusFilter) {
            StatusFilter.ALL -> list
            StatusFilter.PAID -> list.filter { it.feeStatus == FeeStatus.PAID }
            StatusFilter.PARTIAL -> list.filter { it.feeStatus == FeeStatus.PARTIAL }
            StatusFilter.UNPAID -> list.filter { it.feeStatus == FeeStatus.UNPAID }
        }

        // 4. Sort
        when (sort) {
            SortOption.ROLL_NO_ASC -> list.sortedBy { it.rollNo }
            SortOption.NAME_ASC -> list.sortedBy { it.name.lowercase() }
            SortOption.PENDING_DESC -> list.sortedByDescending { it.pendingFees }
            SortOption.TOTAL_FEES_DESC -> list.sortedByDescending { it.totalFees }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun computeDashboardStats(students: List<Student>): DashboardStats {
        val totalExpected = students.sumOf { it.totalFees }
        val totalCollected = students.sumOf { it.totalPaid }
        val totalPending = (totalExpected - totalCollected).coerceAtLeast(0.0)
        val count = students.size
        val paidCount = students.count { it.feeStatus == FeeStatus.PAID }
        val partialCount = students.count { it.feeStatus == FeeStatus.PARTIAL }
        val unpaidCount = students.count { it.feeStatus == FeeStatus.UNPAID }
        val rate = if (totalExpected > 0) ((totalCollected / totalExpected).toFloat()).coerceIn(0f, 1f) else 0f

        val classSummaries = DummyData.classes.map { className ->
            val classStudents = students.filter { it.studentClass.equals(className, ignoreCase = true) }
            val cExpected = classStudents.sumOf { it.totalFees }
            val cCollected = classStudents.sumOf { it.totalPaid }
            val cPending = (cExpected - cCollected).coerceAtLeast(0.0)
            val cRate = if (cExpected > 0) ((cCollected / cExpected).toFloat()).coerceIn(0f, 1f) else 0f

            ClassSummary(
                className = className,
                studentCount = classStudents.size,
                totalExpected = cExpected,
                totalCollected = cCollected,
                totalPending = cPending,
                collectionPercentage = cRate
            )
        }

        return DashboardStats(
            totalExpectedFees = totalExpected,
            totalCollectedFees = totalCollected,
            totalPendingFees = totalPending,
            totalStudents = count,
            paidStudentsCount = paidCount,
            partialStudentsCount = partialCount,
            unpaidStudentsCount = unpaidCount,
            collectionRate = rate,
            classSummaries = classSummaries
        )
    }

    fun selectClass(className: String) {
        _selectedClass.value = className
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: StatusFilter) {
        _selectedStatusFilter.value = filter
    }

    fun setSortOption(sort: SortOption) {
        _sortOption.value = sort
    }

    fun openAddDialog() {
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _isAddDialogOpen.value = false
    }

    fun openEditDialog(student: Student) {
        _editingStudent.value = student
    }

    fun closeEditDialog() {
        _editingStudent.value = null
    }

    fun openReceiptDialog(student: Student) {
        _receiptStudent.value = student
    }

    fun closeReceiptDialog() {
        _receiptStudent.value = null
    }

    fun promptDelete(student: Student) {
        _studentToDelete.value = student
    }

    fun cancelDelete() {
        _studentToDelete.value = null
    }

    fun openResetDialog() {
        _isResetDialogOpen.value = true
    }

    fun closeResetDialog() {
        _isResetDialogOpen.value = false
    }

    // Fast Save / Update for inline editing or action button
    fun updateStudentPayments(
        student: Student,
        firstPaid: Double,
        secondPaid: Double,
        totalFees: Double = student.totalFees
    ) {
        viewModelScope.launch {
            val updated = student.copy(
                firstInstallmentPaid = firstPaid.coerceAtLeast(0.0),
                secondInstallmentPaid = secondPaid.coerceAtLeast(0.0),
                totalFees = totalFees.coerceAtLeast(0.0),
                updatedAt = System.currentTimeMillis()
            )
            repository.updateStudent(updated)
            _userMessage.emit("Updated fee record for ${student.name}")
        }
    }

    fun saveEditedStudent(
        id: Long,
        rollNo: String,
        name: String,
        studentClass: String,
        totalFees: Double,
        firstPaid: Double,
        secondPaid: Double,
        phone: String,
        remarks: String
    ) {
        viewModelScope.launch {
            val updated = Student(
                id = id,
                rollNo = rollNo.trim(),
                name = name.trim(),
                studentClass = studentClass,
                totalFees = totalFees.coerceAtLeast(0.0),
                firstInstallmentPaid = firstPaid.coerceAtLeast(0.0),
                secondInstallmentPaid = secondPaid.coerceAtLeast(0.0),
                parentContact = phone.trim(),
                remarks = remarks.trim(),
                updatedAt = System.currentTimeMillis()
            )
            repository.updateStudent(updated)
            _editingStudent.value = null
            _userMessage.emit("Saved details for ${updated.name}")
        }
    }

    fun addStudent(
        rollNo: String,
        name: String,
        studentClass: String,
        totalFees: Double,
        firstPaid: Double,
        secondPaid: Double,
        phone: String,
        remarks: String
    ) {
        viewModelScope.launch {
            val newStudent = Student(
                rollNo = rollNo.trim().ifEmpty { "ILM-${System.currentTimeMillis() % 1000}" },
                name = name.trim(),
                studentClass = studentClass,
                totalFees = totalFees.coerceAtLeast(0.0),
                firstInstallmentPaid = firstPaid.coerceAtLeast(0.0),
                secondInstallmentPaid = secondPaid.coerceAtLeast(0.0),
                parentContact = phone.trim(),
                remarks = remarks.trim(),
                updatedAt = System.currentTimeMillis()
            )
            repository.insertStudent(newStudent)
            _isAddDialogOpen.value = false
            _userMessage.emit("Added ${newStudent.name} to $studentClass")
        }
    }

    fun confirmDeleteStudent() {
        val target = _studentToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteStudent(target)
            _studentToDelete.value = null
            _userMessage.emit("Removed ${target.name}")
        }
    }

    fun resetToDemoData() {
        viewModelScope.launch {
            repository.resetToDefaultDummyData()
            _isResetDialogOpen.value = false
            _userMessage.emit("Reset fee database to default demo data")
        }
    }
}
