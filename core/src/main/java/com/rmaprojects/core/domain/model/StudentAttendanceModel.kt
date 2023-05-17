package com.rmaprojects.core.domain.model

data class StudentAttendanceModel(
    val studentName: String,
    val studentNis: Int,
    var studentStatus: String,
    val studentYearId: Int
)