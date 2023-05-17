package com.rmaprojects.core.data.source.remote.response.students

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentIdEntity(
    @SerialName("id_student") val studentId: Int?
)