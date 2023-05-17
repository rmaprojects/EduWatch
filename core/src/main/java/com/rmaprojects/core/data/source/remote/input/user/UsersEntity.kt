package com.rmaprojects.core.data.source.remote.input.user


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersEntity(
    @SerialName("email")
    val email: String,
    @SerialName("role")
    val role: String,
    @SerialName("token")
    val token: String? = null,
    @SerialName("created_at")
    val createdAt: String = "now()",
    @SerialName("id")
    val id: String? = null,
)