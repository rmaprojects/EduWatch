package com.rmaprojects.core.data.source.remote.response.attendance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceItemEntity(
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("id_attendance")
    val idAttendance: Int,
    @SerialName("id_student")
    val idStudent: Int,
    @SerialName("name")
    val name: String,
    @SerialName("nis")
    val nis: Int,
    @SerialName("status")
    val status: String,
    @SerialName("parents_token")
    val token: String?
)