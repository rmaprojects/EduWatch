package com.rmaprojects.core.domain.model

data class ChosenStudentModel(
    val studentName: String,
    val nis: Int,
    val studentId: Int,
    val studentYearId: Int?
)