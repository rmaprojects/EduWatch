package com.rmaprojects.core.domain.use_cases.attendance

import com.rmaprojects.core.data.source.remote.input.attendance.AttendanceEntity
import com.rmaprojects.core.data.source.remote.input.attendance.StudentAttendanceEntity
import com.rmaprojects.core.data.source.remote.response.students.StudentsYearsEntity
import com.rmaprojects.core.data.source.remote.response.subject.SubjectEntity
import com.rmaprojects.core.domain.model.StudentAttendanceModel
import com.rmaprojects.core.domain.repository.EduWatchRepository

class AttendanceUseCase(
    private val repository: EduWatchRepository
) {
    suspend fun getSubjectList(): List<SubjectEntity> {
        return repository.getSubjectList()
    }

    suspend fun getStudentByClassList(classYearId: Int): List<StudentsYearsEntity> {
        return repository.getStudentsYears(classYearId)
    }

    suspend fun insertAttendance(
        idClassYear: Int,
        idSubject: Int,
        idTeacher: String,
        information: String,
        latitude: Double?,
        longitude: Double?,
    ): List<AttendanceEntity> {
        return repository.insertAttendance(idClassYear, idSubject, idTeacher, information, latitude, longitude)
    }

    suspend fun insertStudentAttendance(
        idAttendance: Int,
        studentList: List<StudentAttendanceModel>,
        tokenList: List<String?>
    ) {

        val createAttendanceEntity = studentList.map { model ->
            StudentAttendanceEntity(
                idAttendance = idAttendance,
                idStudentYear = model.studentYearId,
                status = model.studentStatus
            )
        }

        repository.insertStudentAttendance(
            createAttendanceEntity,
            tokenList
        )
    }
}