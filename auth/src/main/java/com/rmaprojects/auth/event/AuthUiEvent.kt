package com.rmaprojects.auth.event

import com.rmaprojects.core.data.source.remote.response.students.StudentsEntity

sealed class AuthUiEvent {
    object Loading: AuthUiEvent()
    data class Error(val message: String): AuthUiEvent()
    object Success: AuthUiEvent()
    object EmptyTextField: AuthUiEvent()
}

sealed class FetchStudentUiEvent {
    data class Success(val data: List<StudentsEntity>): FetchStudentUiEvent()
    data class Error(val message: String): FetchStudentUiEvent()
}