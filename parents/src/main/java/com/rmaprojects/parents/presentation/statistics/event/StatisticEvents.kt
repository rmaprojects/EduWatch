package com.rmaprojects.parents.presentation.statistics.event

sealed class StatisticEvents {
    object Loading: StatisticEvents()
    object Success: StatisticEvents()
    data class Error(val message: String): StatisticEvents()
}