package com.rmaprojects.teachers.presentation.student_scoring.input.event

sealed class InputScoringEvent {
    object Loading: InputScoringEvent()
    object Success: InputScoringEvent()
    data class Error(val message: String): InputScoringEvent()
}
