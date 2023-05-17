package com.rmaprojects.core.domain.repository

import com.rmaprojects.core.data.source.remote.input.attendance.AttendanceEntity
import com.rmaprojects.core.data.source.remote.input.attendance.StudentAttendanceEntity
import com.rmaprojects.core.data.source.remote.input.classes.ClassYearEntity
import com.rmaprojects.core.data.source.remote.input.classes.ClassesEntity
import com.rmaprojects.core.data.source.remote.input.grading.GradingEntity
import com.rmaprojects.core.data.source.remote.input.students.StudentYearEntity
import com.rmaprojects.core.data.source.remote.response.attendance.AttendanceItemEntity
import com.rmaprojects.core.data.source.remote.response.attendance.AttendanceView
import com.rmaprojects.core.data.source.remote.response.classes.ClassViewEntity
import com.rmaprojects.core.data.source.remote.response.grades.GradeViewEntity
import com.rmaprojects.core.data.source.remote.response.students.StudentsEntity
import com.rmaprojects.core.data.source.remote.response.students.StudentsYearsEntity
import com.rmaprojects.core.data.source.remote.response.subject.SubjectEntity
import com.rmaprojects.core.utils.UserRole

interface EduWatchRepository {
    suspend fun login(
        email: String,
        password: String
    )

    suspend fun register(
        email: String,
        password: String,
        name: String,
        role: UserRole,
        phoneNumber: String,
        studentId: Int?
    )

    suspend fun getStudents(): List<StudentsEntity>

    suspend fun getStudentDetail(studentId: Int?): StudentsEntity?
    suspend fun insertStudent(
        studentId: Int?,
        name: String,
        nis: Int,
        phoneNumber: String?
    )

    suspend fun getUserDetail(
        userId: String,
        userRole: UserRole
    ): List<Any>

    suspend fun deleteStudent(studentId: Int?)

    suspend fun logOut()

    suspend fun getClassesList(): List<ClassesEntity>

    suspend fun insertClass(grade: Int, vocation: String): List<ClassesEntity>

    suspend fun insertClassYear(year: Int, classId: Int?): List<ClassYearEntity>

    suspend fun getClassYearList(classYearId: Int): List<ClassViewEntity>

    suspend fun getClassYearList(): List<ClassViewEntity>

    suspend fun getStudentsYears(classYearId: Int): List<StudentsYearsEntity>

    suspend fun assignStudentWithSchoolYear(
        studentYearId: Int?,
        classYearId: Int,
        studentId: List<Int>?
    )

    suspend fun removeStudentFromClass(
        studentClassId: Int?
    )

    suspend fun removeClasFromSchoolYears(
        classYearId: Int
    )

    suspend fun getSubjectList(): List<SubjectEntity>

    suspend fun insertAttendance(
        idClassYear: Int,
        idSubject: Int,
        idTeacher: String,
        information: String,
        latitude: Double?,
        longitude: Double?,
    ): List<AttendanceEntity>

    suspend fun insertStudentAttendance(
        studentAttendanceList: List<StudentAttendanceEntity>,
        tokenList: List<String?>
    )

    suspend fun getAttendance(studentId: Int): List<AttendanceItemEntity>

    suspend fun getAttendance(
        studentId: Int,
        currentDay: String,
        nextDay: String
    ): List<AttendanceView>

    suspend fun insertGrading(
        listGradingEntity: List<GradingEntity>
    )

    suspend fun getGrading(
        studentId: Int
    ): List<GradeViewEntity>

}