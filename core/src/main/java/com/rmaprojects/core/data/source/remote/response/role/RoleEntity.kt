package com.rmaprojects.core.data.source.remote.response.role

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleEntity(
    @SerialName("role") val role: String
)