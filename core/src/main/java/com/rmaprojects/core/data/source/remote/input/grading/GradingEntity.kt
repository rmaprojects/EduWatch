package com.rmaprojects.core.data.source.remote.input.grading


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GradingEntity(
    @SerialName("grade")
    val grade: Int,
    @SerialName("id_student_year")
    val idStudentYear: Int,
    @SerialName("id_subject")
    val idSubject: Int,
    @SerialName("type")
    val type: String,
    @SerialName("created_at")
    val createdAt: String = "now()",
    @SerialName("id")
    val id: Int? = null,
)