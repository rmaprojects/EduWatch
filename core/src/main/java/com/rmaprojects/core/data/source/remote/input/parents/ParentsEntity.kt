package com.rmaprojects.core.data.source.remote.input.parents


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParentsEntity(
    @SerialName("id_student")
    val idStudent: Int?,
    @SerialName("name")
    val name: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("id")
    val id: String? = null,
)