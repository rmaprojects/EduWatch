package com.rmaprojects.core.domain.use_cases.assign_student

import com.rmaprojects.core.data.source.remote.input.classes.ClassYearEntity
import com.rmaprojects.core.data.source.remote.input.classes.ClassesEntity
import com.rmaprojects.core.data.source.remote.response.classes.ClassViewEntity
import com.rmaprojects.core.domain.repository.EduWatchRepository

class AssignStudentUseCase(
    private val repository: EduWatchRepository
) {
    suspend fun getClassYearList(classYearId: Int): List<ClassViewEntity> {
        return repository.getClassYearList(classYearId)
    }

    suspend fun getClassYearList(): List<ClassViewEntity> {
        return repository.getClassYearList()
    }

    suspend fun getClassList(): List<ClassesEntity> {
        return repository.getClassesList()
    }

    suspend fun assignStudentWithSchoolYear(
        studentId: List<Int>,
        studentYearId: Int?,
        classYearId: Int
    ) {
        repository.assignStudentWithSchoolYear(studentYearId, classYearId, studentId)
    }

    suspend fun insertClass(grade: Int, vocation: String): List<ClassesEntity> {
        return repository.insertClass(grade, vocation)
    }

    suspend fun insertClasYear(year: Int, classId: Int?): List<ClassYearEntity> {
        return repository.insertClassYear(year, classId)
    }

    suspend fun removeStudentFromClass(studentYearId: Int) {
        repository.removeStudentFromClass(studentYearId)
    }

    suspend fun removeClassFromSchoolYears(classYearId: Int) {
        repository.removeClasFromSchoolYears(classYearId)
    }
}