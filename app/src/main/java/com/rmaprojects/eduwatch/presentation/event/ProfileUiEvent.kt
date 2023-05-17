package com.rmaprojects.eduwatch.presentation.event

sealed class ProfileUiEvent {
    object Loading: ProfileUiEvent()
    object Success: ProfileUiEvent()
    data class Error(val message: String): ProfileUiEvent()
}
