package com.rmaprojects.core.data.source.remote.response.parents


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParentsEntity(
    @SerialName("id")
    val id: String,
    @SerialName("name_parents")
    val nameParents: String,
    @SerialName("name_student")
    val nameStudent: String,
    @SerialName("nis")
    val nis: Int,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("token")
    val token: String?
)