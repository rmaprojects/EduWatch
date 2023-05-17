package com.rmaprojects.core.data.source.remote.input.teacher


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherEntity(
    @SerialName("is_admin")
    val isAdmin: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("id")
    val id: String? = null,
)