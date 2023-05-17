package com.rmaprojects.core.data.source.remote.input.classes


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassesEntity(
    @SerialName("grade")
    val grade: Int,
    @SerialName("vocation")
    val vocation: String,
    @SerialName("id_class")
    val idClass: Int? = null,
)