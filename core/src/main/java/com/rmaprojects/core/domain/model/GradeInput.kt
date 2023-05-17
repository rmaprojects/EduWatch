package com.rmaprojects.core.domain.model

data class GradeInput (
    val subjectName: String,
    val subjectId: Int,
    val studentYearId: Int,
    val gradeType: String = "PTS",
    var grade: Int = 0,
)