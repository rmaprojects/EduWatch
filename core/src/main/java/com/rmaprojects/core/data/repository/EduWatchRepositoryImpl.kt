package com.rmaprojects.core.data.repository

import com.rmaprojects.core.data.source.remote.RemoteDataSource
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
import com.rmaprojects.core.domain.repository.EduWatchRepository
import com.rmaprojects.core.utils.UserRole
import javax.inject.Inject

class EduWatchRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : EduWatchRepository {
    override suspend fun login(email: String, password: String) {
        remoteDataSource.login(email, password)
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        role: UserRole,
        phoneNumber: String,
        studentId: Int?
    ) {
        remoteDataSource.register(email, password)
        remoteDataSource.insertUserToDB(email, role)
        remoteDataSource.assignUserRole(name, role, phoneNumber, studentId)
    }

    override suspend fun getStudents(): List<StudentsEntity> {
        return remoteDataSource.getStudents()
    }

    override suspend fun getStudentDetail(studentId: Int?): StudentsEntity? {
        return remoteDataSource.getStudentDetail(studentId)
    }

    override suspend fun insertStudent(
        studentId: Int?,
        name: String,
        nis: Int,
        phoneNumber: String?
    ) {
        remoteDataSource.insertStudent(studentId, name, nis, phoneNumber)
    }

    override suspend fun getUserDetail(userId: String, userRole: UserRole): List<Any> {
        return remoteDataSource.getUserProfile(userId, userRole)
    }

    override suspend fun deleteStudent(studentId: Int?) {
        remoteDataSource.deleteStudent(studentId)
    }

    override suspend fun insertClass(grade: Int, vocation: String): List<ClassesEntity> {
        return remoteDataSource.insertClass(grade, vocation)
    }

    override suspend fun insertClassYear(year: Int, classId: Int?): List<ClassYearEntity> {
        return remoteDataSource.insertClassYearList(year, classId)
    }

    override suspend fun logOut() {
        remoteDataSource.logOut()
    }

    override suspend fun getClassesList(): List<ClassesEntity> {
        return remoteDataSource.getClassesList()
    }

    override suspend fun getClassYearList(classYearId: Int): List<ClassViewEntity> {
        return remoteDataSource.getClassYearList(classYearId)
    }

    override suspend fun getClassYearList(): List<ClassViewEntity> {
        return remoteDataSource.getClassYearList()
    }

    override suspend fun getStudentsYears(classYearId: Int): List<StudentsYearsEntity> {
        return remoteDataSource.getStudentYears(classYearId)
    }

    override suspend fun assignStudentWithSchoolYear(
        studentYearId: Int?,
        classYearId: Int,
        studentId: List<Int>?
    ) {
        return remoteDataSource.assignStudentWithSchoolYear(studentYearId, classYearId, studentId)
    }

    override suspend fun removeStudentFromClass(studentClassId: Int?) {
        return remoteDataSource.removeStudentFromClass(studentClassId)
    }

    override suspend fun removeClasFromSchoolYears(classYearId: Int) {
        return remoteDataSource.removeClassFromSchoolYears(classYearId)
    }

    override suspend fun getSubjectList(): List<SubjectEntity> {
        return remoteDataSource.getSubjectList()
    }

    override suspend fun insertAttendance(
        idClassYear: Int,
        idSubject: Int,
        idTeacher: String,
        information: String,
        latitude: Double?,
        longitude: Double?,
    ): List<AttendanceEntity> {
        return remoteDataSource.insertAttendance(idClassYear, idSubject, idTeacher, information, latitude, longitude)
    }

    override suspend fun insertStudentAttendance(studentAttendanceList: List<StudentAttendanceEntity>, tokenList: List<String?>) {
        remoteDataSource.insertStudentAttendance(studentAttendanceList, tokenList)
    }

    override suspend fun getAttendance(studentId: Int): List<AttendanceItemEntity> {
        return remoteDataSource.getAttendance(studentId)
    }

    override suspend fun getAttendance(studentId: Int, currentDay: String, nextDay: String): List<AttendanceView> {
        return remoteDataSource.getAttendance(studentId, currentDay, nextDay)
    }

    override suspend fun insertGrading(listGradingEntity: List<GradingEntity>) {
        remoteDataSource.insertGrading(listGradingEntity)
    }

    override suspend fun getGrading(
        studentId: Int,
    ): List<GradeViewEntity> {
        return remoteDataSource.getGrading(studentId)
    }
}