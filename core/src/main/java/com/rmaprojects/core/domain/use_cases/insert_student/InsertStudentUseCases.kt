package com.rmaprojects.core.domain.use_cases.insert_student

import com.rmaprojects.core.data.source.remote.response.students.StudentsEntity
import com.rmaprojects.core.domain.repository.EduWatchRepository

class InsertStudentUseCases(
    private val repository: EduWatchRepository
) {
    suspend fun insertStudent(studentId: Int?, name: String, nis: Int, phoneNumber: String) {
        repository.insertStudent(
            studentId,
            name,
            nis,
            phoneNumber
        )
    }

    suspend fun getStudents(): List<StudentsEntity> {
        return repository.getStudents()
    }

    suspend fun deleteStudent(studentId: Int?) {
        repository.deleteStudent(studentId)
    }

    suspend fun getStudentDetail(studentId: Int?): StudentsEntity? {
        return repository.getStudentDetail(studentId)
    }
}