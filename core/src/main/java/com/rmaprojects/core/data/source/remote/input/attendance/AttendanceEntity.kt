package com.rmaprojects.core.data.source.remote.input.attendance


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceEntity(
    @SerialName("id_class_year")
    val idClassYear: Int,
    @SerialName("id_subject")
    val idSubject: Int,
    @SerialName("id_teacher")
    val idTeacher: String,
    @SerialName("information")
    val information: String,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("created_at")
    val createdAt: String = "now()",
    @SerialName("id")
    val id: Int? = null,
    @SerialName("image_url")
    val imageUrl: String? = "",
)