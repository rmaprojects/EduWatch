package com.rmaprojects.core.data.source.remote.response.teacher


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeachersEntity(
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("role")
    val role: String,
    @SerialName("token")
    val token: String?
)