package com.rmaprojects.core.domain.use_cases.auth

import com.rmaprojects.core.data.source.remote.response.students.StudentsEntity
import com.rmaprojects.core.domain.repository.EduWatchRepository
import com.rmaprojects.core.utils.UserRole

class AuthUseCases(
    private val repository: EduWatchRepository
) {
    suspend fun login(email: String, password: String) {
        repository.login(email, password)
    }

    suspend fun logOut() {
        repository.logOut()
    }

    suspend fun register(
        email: String,
        password: String,
        name: String,
        role: UserRole,
        phoneNumber: String,
        studentId: Int?
    ) {
        repository.register(email, password, name, role, phoneNumber, studentId)
    }

    suspend fun getStudents(): List<StudentsEntity> {
        return repository.getStudents()
    }
}