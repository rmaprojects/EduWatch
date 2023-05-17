package com.rmaprojects.core.data.source.remote.response.grades


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GradeViewEntity(
    @SerialName("class")
    val classX: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("grade")
    val grade: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("id_class_year")
    val idClassYear: Int,
    @SerialName("id_student")
    val idStudent: Int,
    @SerialName("name")
    val name: String,
    @SerialName("name_subject")
    val nameSubject: String,
    @SerialName("type")
    val type: String,
    @SerialName("vocation")
    val vocation: String,
    @SerialName("year")
    val year: Int
)