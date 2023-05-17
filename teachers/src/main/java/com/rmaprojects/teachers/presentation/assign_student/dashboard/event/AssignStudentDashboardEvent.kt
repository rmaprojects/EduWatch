package com.rmaprojects.teachers.presentation.assign_student.dashboard.event

sealed class AssignStudentDashboardEvent {
    object Loading: AssignStudentDashboardEvent()
    data class Error(val message: String): AssignStudentDashboardEvent()
    object Success: AssignStudentDashboardEvent()
}

sealed class DeleteClassEvent {
    object Loading: DeleteClassEvent()
    object Success: DeleteClassEvent()
    data class Error(val message: String): DeleteClassEvent()
}