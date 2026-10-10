package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FeeStatus {
    PAID,
    PARTIAL,
    UNPAID
}

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val rollNo: String,
    val name: String,
    val studentClass: String, // "Nursery", "Jr. KG", "Sr. KG", "1st Standard", "2nd Standard"
    val totalFees: Double,
    val firstInstallmentPaid: Double = 0.0,
    val secondInstallmentPaid: Double = 0.0,
    val parentContact: String = "",
    val remarks: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalPaid: Double
        get() = firstInstallmentPaid + secondInstallmentPaid

    val pendingFees: Double
        get() = (totalFees - totalPaid).coerceAtLeast(0.0)

    val feeStatus: FeeStatus
        get() = when {
            pendingFees <= 0.0 -> FeeStatus.PAID
            totalPaid > 0.0 -> FeeStatus.PARTIAL
            else -> FeeStatus.UNPAID
        }

    val paymentProgress: Float
        get() = if (totalFees > 0) ((totalPaid / totalFees).toFloat()).coerceIn(0f, 1f) else 0f
}
