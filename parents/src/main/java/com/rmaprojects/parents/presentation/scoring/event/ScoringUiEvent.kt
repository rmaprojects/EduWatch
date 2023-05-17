package com.rmaprojects.parents.presentation.scoring.event

sealed class ScoringUiEvent {
    object Loading: ScoringUiEvent()
    object Success: ScoringUiEvent()
    object EmptyList: ScoringUiEvent()
    data class Error(val message: String): ScoringUiEvent()
}