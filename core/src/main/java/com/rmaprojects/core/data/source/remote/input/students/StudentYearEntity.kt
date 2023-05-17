package com.rmaprojects.core.data.source.remote.input.students

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentYearEntity(
    @SerialName("id_class_name") val idClassYear: Int,
    @SerialName("id_student") val studentId: Int?,
    @SerialName("id") val id: Int? = null
)