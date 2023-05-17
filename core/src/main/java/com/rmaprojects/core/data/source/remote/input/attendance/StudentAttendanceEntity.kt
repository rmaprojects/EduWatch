package com.rmaprojects.core.data.source.remote.input.attendance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentAttendanceEntity(
    @SerialName("id_attendance")
    val idAttendance: Int,
    @SerialName("id_student_year")
    val idStudentYear: Int,
    @SerialName("status")
    val status: String = "Hadir",
    @SerialName("id")
    val id: Int? = null,
)