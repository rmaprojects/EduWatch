package com.rmaprojects.core.domain.use_cases.grading

import com.rmaprojects.core.data.source.remote.input.grading.GradingEntity
import com.rmaprojects.core.data.source.remote.response.grades.GradeViewEntity
import com.rmaprojects.core.domain.model.GradeInput
import com.rmaprojects.core.domain.repository.EduWatchRepository

class GradingUseCase(
    private val repository: EduWatchRepository
) {
    suspend fun insertGrading(
        listGrading: List<GradeInput>,
        gradingType: String
    ) {
        val createGradingEntity = listGrading.map {
            GradingEntity(
                grade = it.grade,
                idStudentYear = it.studentYearId,
                idSubject = it.subjectId,
                type = gradingType
            )
        }

        repository.insertGrading(createGradingEntity)
    }

    suspend fun getGrading(
        studentId: Int,
    ): List<GradeViewEntity> {
        return repository.getGrading(studentId)
    }
}