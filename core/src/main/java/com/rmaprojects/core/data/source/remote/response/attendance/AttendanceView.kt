package com.rmaprojects.core.data.source.remote.response.attendance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceView(
    @SerialName("class_vocation")
    val classVocation: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("id")
    val id: Int,
    @SerialName("id_class_year")
    val idClassYear: Int,
    @SerialName("id_student")
    val idStudent: Int,
    @SerialName("information")
    val information: String,
    @SerialName("name_class")
    val nameClass: Int,
    @SerialName("name_subject")
    val nameSubject: String,
    @SerialName("name_teacher")
    val nameTeacher: String
)