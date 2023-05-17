package com.rmaprojects.teachers.presentation.student_list.dashboard.events

sealed class StudentListDashboardUiEvent {
    object Loading: StudentListDashboardUiEvent()
    object Success: StudentListDashboardUiEvent()
    data class Error(val message: String): StudentListDashboardUiEvent()
}

sealed class DeleteStudentEvent {
    object Loading: DeleteStudentEvent()
    object Success: DeleteStudentEvent()
    data class Error(val message: String): DeleteStudentEvent()
}