package com.rmaprojects.core.data.source.remote.response.classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassViewEntity(
    @SerialName("grade")
    val grade: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("vocation")
    val vocation: String,
    @SerialName("year")
    val year: Int
)