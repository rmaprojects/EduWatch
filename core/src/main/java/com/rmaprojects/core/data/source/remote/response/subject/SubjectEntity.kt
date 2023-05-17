package com.rmaprojects.core.data.source.remote.response.subject

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubjectEntity(
    val id: Int,
    @SerialName("name_subject") val subjectName: String
)