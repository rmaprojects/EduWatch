package com.rmaprojects.teachers.presentation.attendance.event

sealed class AttendanceEvent {
    object Loading: AttendanceEvent()
    object Success: AttendanceEvent()
    data class Error(val message: String): AttendanceEvent()
}