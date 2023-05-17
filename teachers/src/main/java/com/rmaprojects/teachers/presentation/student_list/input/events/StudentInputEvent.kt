package com.rmaprojects.teachers.presentation.student_list.input.events

sealed class StudentInputEvent {
    object Loading: StudentInputEvent()
    data class Error(val message: String): StudentInputEvent()
    object Success: StudentInputEvent()
}
