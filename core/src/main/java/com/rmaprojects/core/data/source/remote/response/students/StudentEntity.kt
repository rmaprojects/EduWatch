package com.rmaprojects.core.data.source.remote.response.students

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentsEntity(
    @SerialName("name")
    val name: String,
    @SerialName("nis")
    val nis: Int,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("id")
    val id: Int? = null,
)