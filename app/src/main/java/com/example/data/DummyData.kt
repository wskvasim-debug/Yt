package com.example.data

import com.example.model.Student

object DummyData {
    val classes = listOf(
        "Nursery",
        "Jr. KG",
        "Sr. KG",
        "1st Standard",
        "2nd Standard"
    )

    fun getInitialStudents(): List<Student> {
        return listOf(
            // Nursery (Total Fees: ₹16,000 | 1st Inst: ₹8,000, 2nd Inst: ₹8,000)
            Student(
                id = 1,
                rollNo = "NUR-101",
                name = "Aarav Sharma",
                studentClass = "Nursery",
                totalFees = 16000.0,
                firstInstallmentPaid = 8000.0,
                secondInstallmentPaid = 8000.0,
                parentContact = "+91 98765 43210",
                remarks = "Full payment completed"
            ),
            Student(
                id = 2,
                rollNo = "NUR-102",
                name = "Fatima Khan",
                studentClass = "Nursery",
                totalFees = 16000.0,
                firstInstallmentPaid = 8000.0,
                secondInstallmentPaid = 4000.0,
                parentContact = "+91 98123 45678",
                remarks = "2nd installment partial"
            ),
            Student(
                id = 3,
                rollNo = "NUR-103",
                name = "Zaid Merchant",
                studentClass = "Nursery",
                totalFees = 16000.0,
                firstInstallmentPaid = 0.0,
                secondInstallmentPaid = 0.0,
                parentContact = "+91 98989 12121",
                remarks = "Pending fee reminder sent"
            ),
            Student(
                id = 4,
                rollNo = "NUR-104",
                name = "Ananya Patel",
                studentClass = "Nursery",
                totalFees = 16000.0,
                firstInstallmentPaid = 8000.0,
                secondInstallmentPaid = 0.0,
                parentContact = "+91 97654 32109",
                remarks = "1st installment cleared"
            ),

            // Jr. KG (Total Fees: ₹18,000 | 1st Inst: ₹9,000, 2nd Inst: ₹9,000)
            Student(
                id = 5,
                rollNo = "JKG-201",
                name = "Mohammed Ayaan",
                studentClass = "Jr. KG",
                totalFees = 18000.0,
                firstInstallmentPaid = 9000.0,
                secondInstallmentPaid = 9000.0,
                parentContact = "+91 98220 11223",
                remarks = "Paid via UPI"
            ),
            Student(
                id = 6,
                rollNo = "JKG-202",
                name = "Rida Shaikh",
                studentClass = "Jr. KG",
                totalFees = 18000.0,
                firstInstallmentPaid = 9000.0,
                secondInstallmentPaid = 4500.0,
                parentContact = "+91 98450 33445",
                remarks = "Promised balance next month"
            ),
            Student(
                id = 7,
                rollNo = "JKG-203",
                name = "Kabir Deshmukh",
                studentClass = "Jr. KG",
                totalFees = 18000.0,
                firstInstallmentPaid = 0.0,
                secondInstallmentPaid = 0.0,
                parentContact = "+91 98777 88990",
                remarks = "Financial aid applicant"
            ),
            Student(
                id = 8,
                rollNo = "JKG-204",
                name = "Sara Ansari",
                studentClass = "Jr. KG",
                totalFees = 18000.0,
                firstInstallmentPaid = 9000.0,
                secondInstallmentPaid = 9000.0,
                parentContact = "+91 98333 44556",
                remarks = "Annual discount applied"
            ),

            // Sr. KG (Total Fees: ₹20,000 | 1st Inst: ₹10,000, 2nd Inst: ₹10,000)
            Student(
                id = 9,
                rollNo = "SKG-301",
                name = "Hamza Qureshi",
                studentClass = "Sr. KG",
                totalFees = 20000.0,
                firstInstallmentPaid = 10000.0,
                secondInstallmentPaid = 10000.0,
                parentContact = "+91 98111 22334",
                remarks = "All clear"
            ),
            Student(
                id = 10,
                rollNo = "SKG-302",
                name = "Myra Sen",
                studentClass = "Sr. KG",
                totalFees = 20000.0,
                firstInstallmentPaid = 10000.0,
                secondInstallmentPaid = 5000.0,
                parentContact = "+91 98666 77889",
                remarks = "Cheque cleared for 1st inst"
            ),
            Student(
                id = 11,
                rollNo = "SKG-303",
                name = "Ibrahim Siddiqui",
                studentClass = "Sr. KG",
                totalFees = 20000.0,
                firstInstallmentPaid = 0.0,
                secondInstallmentPaid = 0.0,
                parentContact = "+91 98555 66778",
                remarks = "Contact parent"
            ),
            Student(
                id = 12,
                rollNo = "SKG-304",
                name = "Diya Kulkarni",
                studentClass = "Sr. KG",
                totalFees = 20000.0,
                firstInstallmentPaid = 10000.0,
                secondInstallmentPaid = 10000.0,
                parentContact = "+91 98444 55667",
                remarks = "Net banking receipt verified"
            ),

            // 1st Standard (Total Fees: ₹22,000 | 1st Inst: ₹11,000, 2nd Inst: ₹11,000)
            Student(
                id = 13,
                rollNo = "1ST-401",
                name = "Omar Farooq",
                studentClass = "1st Standard",
                totalFees = 22000.0,
                firstInstallmentPaid = 11000.0,
                secondInstallmentPaid = 11000.0,
                parentContact = "+91 98333 77112",
                remarks = "Term 1 & 2 cleared"
            ),
            Student(
                id = 14,
                rollNo = "1ST-402",
                name = "Zoya Malik",
                studentClass = "1st Standard",
                totalFees = 22000.0,
                firstInstallmentPaid = 11000.0,
                secondInstallmentPaid = 6000.0,
                parentContact = "+91 98222 33445",
                remarks = "5000 remaining"
            ),
            Student(
                id = 15,
                rollNo = "1ST-403",
                name = "Vivaan Joshi",
                studentClass = "1st Standard",
                totalFees = 22000.0,
                firstInstallmentPaid = 0.0,
                secondInstallmentPaid = 0.0,
                parentContact = "+91 98111 55667",
                remarks = "Unpaid - Overdue"
            ),
            Student(
                id = 16,
                rollNo = "1ST-404",
                name = "Alisha Sayyed",
                studentClass = "1st Standard",
                totalFees = 22000.0,
                firstInstallmentPaid = 11000.0,
                secondInstallmentPaid = 11000.0,
                parentContact = "+91 98999 88112",
                remarks = "Receipt #ILM-404 issued"
            ),

            // 2nd Standard (Total Fees: ₹24,000 | 1st Inst: ₹12,000, 2nd Inst: ₹12,000)
            Student(
                id = 17,
                rollNo = "2ND-501",
                name = "Bilal Ahmed",
                studentClass = "2nd Standard",
                totalFees = 24000.0,
                firstInstallmentPaid = 12000.0,
                secondInstallmentPaid = 12000.0,
                parentContact = "+91 98777 44332",
                remarks = "Paid in full"
            ),
            Student(
                id = 18,
                rollNo = "2ND-502",
                name = "Khadija Mirza",
                studentClass = "2nd Standard",
                totalFees = 24000.0,
                firstInstallmentPaid = 12000.0,
                secondInstallmentPaid = 8000.0,
                parentContact = "+91 98666 55443",
                remarks = "4000 balance in 2nd inst"
            ),
            Student(
                id = 19,
                rollNo = "2ND-503",
                name = "Reyansh Gupta",
                studentClass = "2nd Standard",
                totalFees = 24000.0,
                firstInstallmentPaid = 0.0,
                secondInstallmentPaid = 0.0,
                parentContact = "+91 98555 44332",
                remarks = "Parent requested installment plan"
            ),
            Student(
                id = 20,
                rollNo = "2ND-504",
                name = "Maryam Syed",
                studentClass = "2nd Standard",
                totalFees = 24000.0,
                firstInstallmentPaid = 12000.0,
                secondInstallmentPaid = 12000.0,
                parentContact = "+91 98444 33221",
                remarks = "Receipt #ILM-504 issued"
            )
        )
    }
}
