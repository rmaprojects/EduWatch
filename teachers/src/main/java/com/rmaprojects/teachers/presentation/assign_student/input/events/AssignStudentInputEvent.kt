package com.rmaprojects.teachers.presentation.assign_student.input.events

sealed class AssignStudentInputEvent {
    object Loading: AssignStudentInputEvent()
    object Success: AssignStudentInputEvent()
    data class Error(val message: String): AssignStudentInputEvent()
}