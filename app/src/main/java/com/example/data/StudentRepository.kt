package com.example.data

import com.example.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StudentRepository(private val studentDao: StudentDao) {

    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()

    fun getStudentsByClass(studentClass: String): Flow<List<Student>> {
        return studentDao.getStudentsByClass(studentClass)
    }

    suspend fun insertStudent(student: Student): Long = withContext(Dispatchers.IO) {
        studentDao.insertStudent(student)
    }

    suspend fun updateStudent(student: Student) = withContext(Dispatchers.IO) {
        studentDao.updateStudent(student)
    }

    suspend fun deleteStudent(student: Student) = withContext(Dispatchers.IO) {
        studentDao.deleteStudent(student)
    }

    suspend fun resetToDefaultDummyData() = withContext(Dispatchers.IO) {
        studentDao.clearAll()
        studentDao.insertAll(DummyData.getInitialStudents())
    }

    suspend fun ensureDataInitialized() = withContext(Dispatchers.IO) {
        if (studentDao.getStudentCount() == 0) {
            studentDao.insertAll(DummyData.getInitialStudents())
        }
    }
}
