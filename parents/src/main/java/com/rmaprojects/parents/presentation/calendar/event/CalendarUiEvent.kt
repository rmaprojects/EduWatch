package com.rmaprojects.parents.presentation.calendar.event

sealed class CalendarUiEvent {
    object Loading: CalendarUiEvent()
    object Success: CalendarUiEvent()
    object EmptyTimeline: CalendarUiEvent()
    data class Error(val message: String): CalendarUiEvent()
}
