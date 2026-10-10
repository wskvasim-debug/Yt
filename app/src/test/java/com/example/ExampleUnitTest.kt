package com.example

import com.example.data.DummyData
import com.example.model.FeeStatus
import com.example.model.Student
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testFeeCalculationsAndStatus() {
        // Paid student
        val paidStudent = Student(
            id = 1,
            rollNo = "NUR-101",
            name = "Aarav Sharma",
            studentClass = "Nursery",
            totalFees = 16000.0,
            firstInstallmentPaid = 8000.0,
            secondInstallmentPaid = 8000.0
        )
        assertEquals(16000.0, paidStudent.totalPaid, 0.01)
        assertEquals(0.0, paidStudent.pendingFees, 0.01)
        assertEquals(FeeStatus.PAID, paidStudent.feeStatus)

        // Partial student
        val partialStudent = Student(
            id = 2,
            rollNo = "NUR-102",
            name = "Fatima Khan",
            studentClass = "Nursery",
            totalFees = 16000.0,
            firstInstallmentPaid = 8000.0,
            secondInstallmentPaid = 4000.0
        )
        assertEquals(12000.0, partialStudent.totalPaid, 0.01)
        assertEquals(4000.0, partialStudent.pendingFees, 0.01)
        assertEquals(FeeStatus.PARTIAL, partialStudent.feeStatus)

        // Unpaid student
        val unpaidStudent = Student(
            id = 3,
            rollNo = "NUR-103",
            name = "Zaid Merchant",
            studentClass = "Nursery",
            totalFees = 16000.0,
            firstInstallmentPaid = 0.0,
            secondInstallmentPaid = 0.0
        )
        assertEquals(0.0, unpaidStudent.totalPaid, 0.01)
        assertEquals(16000.0, unpaidStudent.pendingFees, 0.01)
        assertEquals(FeeStatus.UNPAID, unpaidStudent.feeStatus)
    }

    @Test
    fun testDummyDataCoverage() {
        val initialStudents = DummyData.getInitialStudents()
        assertEquals(20, initialStudents.size)

        val classes = initialStudents.map { it.studentClass }.distinct()
        assertEquals(5, classes.size)
        assert(classes.contains("Nursery"))
        assert(classes.contains("Jr. KG"))
        assert(classes.contains("Sr. KG"))
        assert(classes.contains("1st Standard"))
        assert(classes.contains("2nd Standard"))
    }
}
