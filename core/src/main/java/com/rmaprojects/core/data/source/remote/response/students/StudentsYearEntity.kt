package com.rmaprojects.core.data.source.remote.response.students

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentsYearsEntity(
    @SerialName("grade")
    val grade: Int,
    @SerialName("id_class_year")
    val idClassYear: Int,
    @SerialName("name_student")
    val nameStudent: String,
    @SerialName("nis")
    val nis: Int,
    @SerialName("vocation")
    val vocation: String,
    @SerialName("year")
    val year: Int,
    @SerialName("parents_token")
    val token: String?,
    @SerialName("id_student")
    val idStudent: Int? = null,
    @SerialName("id")
    val id: Int? = null,
)