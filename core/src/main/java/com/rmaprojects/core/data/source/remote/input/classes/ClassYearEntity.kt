package com.rmaprojects.core.data.source.remote.input.classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassYearEntity(
    val year: Int,
    @SerialName("id_class") val classId: Int?,
    @SerialName("id") val id: Int? = null
)