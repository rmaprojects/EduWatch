package com.rmaprojects.teachers.presentation.student_scoring.dashboard.event

sealed class DashboardScoringEvent {
    object Loading: DashboardScoringEvent()
    object Success: DashboardScoringEvent()
    data class Error(val message: String): DashboardScoringEvent()
}
