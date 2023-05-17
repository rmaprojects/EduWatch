package com.rmaprojects.core.data.source.remote

import android.content.Context
import android.util.Log
import com.fcmsender.FCMSender
import com.google.firebase.messaging.FirebaseMessaging
import com.rmaprojects.core.BuildConfig
import com.rmaprojects.core.data.source.local.LocalUser
import com.rmaprojects.core.data.source.remote.input.attendance.AttendanceEntity
import com.rmaprojects.core.data.source.remote.input.attendance.StudentAttendanceEntity
import com.rmaprojects.core.data.source.remote.input.classes.ClassYearEntity
import com.rmaprojects.core.data.source.remote.input.students.StudentYearEntity
import com.rmaprojects.core.data.source.remote.input.classes.ClassesEntity
import com.rmaprojects.core.data.source.remote.input.grading.GradingEntity
import com.rmaprojects.core.data.source.remote.input.parents.ParentsEntity
import com.rmaprojects.core.data.source.remote.input.teacher.TeacherEntity
import com.rmaprojects.core.data.source.remote.input.user.UsersEntity
import com.rmaprojects.core.data.source.remote.response.attendance.AttendanceItemEntity
import com.rmaprojects.core.data.source.remote.response.attendance.AttendanceView
import com.rmaprojects.core.data.source.remote.response.classes.ClassViewEntity
import com.rmaprojects.core.data.source.remote.response.grades.GradeViewEntity
import com.rmaprojects.core.data.source.remote.response.role.RoleEntity
import com.rmaprojects.core.data.source.remote.response.students.StudentIdEntity
import com.rmaprojects.core.data.source.remote.response.students.StudentsEntity
import com.rmaprojects.core.data.source.remote.response.students.StudentsYearsEntity
import com.rmaprojects.core.data.source.remote.response.subject.SubjectEntity
import com.rmaprojects.core.data.source.remote.response.teacher.TeachersEntity
import com.rmaprojects.core.data.source.remote.supabase.*
import com.rmaprojects.core.utils.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotiations.SupabaseExperimental
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Returning
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import com.rmaprojects.core.data.source.remote.response.parents.ParentsEntity as ParentsResponse

@Singleton
class RemoteDataSource @Inject constructor(
    private val client: SupabaseClient,
    @ApplicationContext private val context: Context,
    private val fBaseMessaging: FirebaseMessaging
) {
    suspend fun logOut() {
        return client.gotrue.sessionManager.deleteSession()
    }

    suspend fun login(
        email: String,
        password: String
    ) {
        client.gotrue.loginWith(Email) {
            this.email = email
            this.password = password
        }
        val token = fBaseMessaging.token.await()
        val user = client.gotrue.currentSessionOrNull()?.user
        client.postgrest[TABLE_USERS]
            .update(
                {
                    UsersEntity::token setTo token
                }
            ) {
                UsersEntity::id eq user?.id
            }
        val roleResult = client.postgrest[TABLE_USERS]
            .select("role") {
                UsersEntity::id eq user?.id
            }
        val role = roleResult.decodeList<RoleEntity>()[0].role
        if (role == "Wali Murid") {
            val studentId = client.postgrest[TABLE_PARENTS]
                .select("id_student") {
                    UsersEntity::id eq user?.id
                }
                .decodeList<StudentIdEntity>()
            LocalUser.studentId = studentId.first().studentId.toString()
        }
        LocalUser.role = UserRole.values().first { it.roleValue == role }
        LocalUser.id = user?.id
    }

    suspend fun register(
        email: String,
        password: String,
    ) {
        client.gotrue.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun insertUserToDB(
        email: String,
        role: UserRole,
    ) {
        val token = fBaseMessaging.token.await()
        val user = client.gotrue.currentSessionOrNull()?.user
        client.postgrest[TABLE_USERS]
            .insert(
                UsersEntity(
                    email = email,
                    role = UserRole.values().first { it.roleValue == role.roleValue }.roleValue,
                    token = token,
                    id = user?.id
                )
            )
    }

    suspend fun getUserProfile(
        userId: String,
        role: UserRole
    ): List<Any> {
        return when (role) {
            UserRole.PARENTS -> {
                client.postgrest[VIEW_PARENTS]
                    .select {
                        ParentsResponse::id eq userId
                    }
                    .decodeList<ParentsResponse>()
            }
            UserRole.TEACHERS -> {
                client.postgrest[VIEW_TEACHERS]
                    .select {
                        TeachersEntity::id eq userId
                    }
                    .decodeList<TeachersEntity>()
            }
            UserRole.NONE -> {
                throw Exception("")
            }
        }
    }

    suspend fun assignUserRole(
        name: String,
        role: UserRole,
        phoneNumber: String,
        studentId: Int?
    ) {
        val user = client.gotrue.currentSessionOrNull()?.user
        when (role) {
            UserRole.NONE -> {
                throw Exception("Role not Selected or unknown")
            }
            UserRole.TEACHERS -> {
                client.postgrest[TABLE_TEACHER]
                    .insert(
                        TeacherEntity(
                            isAdmin = false,
                            name = name,
                            id = user?.id
                        )
                    )
                LocalUser.role = UserRole.TEACHERS
                LocalUser.id = user?.id
            }
            UserRole.PARENTS -> {
                client.postgrest[TABLE_PARENTS]
                    .insert(
                        ParentsEntity(
                            idStudent = studentId,
                            name = name,
                            phoneNumber = phoneNumber,
                            id = user?.id
                        )
                    )
                LocalUser.role = UserRole.PARENTS
                LocalUser.id = user?.id
                LocalUser.studentId = studentId.toString()
            }
        }
    }

    suspend fun getStudents(): List<StudentsEntity> {
        return client.postgrest[TABLE_STUDENT]
            .select("*").decodeList()
    }

    suspend fun getStudentDetail(studentId: Int?): StudentsEntity? {
        return client.postgrest[TABLE_STUDENT]
            .select { StudentsEntity::id eq studentId }.decodeAsOrNull()
    }

    suspend fun deleteStudent(studentId: Int?) {
        client.postgrest[TABLE_STUDENT]
            .delete {
                StudentsEntity::id eq studentId
            }
    }

    suspend fun insertStudent(studentId: Int?, name: String, nis: Int, phoneNumber: String?) {
        client.postgrest[TABLE_STUDENT]
            .insert(
                StudentsEntity(
                    name = name,
                    nis = nis,
                    phoneNumber = phoneNumber ?: "",
                    id = studentId
                ),
                upsert = true
            )
    }

    suspend fun insertClass(grade: Int, vocation: String): List<ClassesEntity> {
        val result = client.postgrest[TABLE_CLASS]
            .insert(
                ClassesEntity(
                    grade = grade,
                    vocation = vocation
                ),
                returning = Returning.REPRESENTATION
            )
        return result.decodeList()
    }

    suspend fun insertClassYearList(
        year: Int,
        classId: Int?
    ): List<ClassYearEntity> {
        val result = client.postgrest[TABLE_CLASS_YEAR]
            .insert(
                ClassYearEntity(
                    year,
                    classId,
                ),
                returning = Returning.REPRESENTATION
            )
        return result.decodeList()
    }

    suspend fun getClassesList(): List<ClassesEntity> {
        val result = client.postgrest[TABLE_CLASS]
            .select("*")
        return result.decodeList()
    }

    suspend fun getClassYearList(classYearId: Int): List<ClassViewEntity> {
        val result = client.postgrest[VIEW_CLASSES]
            .select("*") {
                ClassViewEntity::id eq classYearId
            }
        return result.decodeList()
    }
    suspend fun getClassYearList(): List<ClassViewEntity> {
        val result = client.postgrest[VIEW_CLASSES]
            .select("*")
        return result.decodeList()
    }

    suspend fun assignStudentWithSchoolYear(
        studentYearId: Int?,
        classYearId: Int,
        studentId: List<Int>?
    ) {

        val list = mutableListOf<StudentYearEntity>()

        studentId?.forEach {
            list.add(
                StudentYearEntity(
                    classYearId,
                    it,
                    studentYearId
                )
            )
        }


        client.postgrest[TABLE_STUDENT_YEAR]
            .insert(
                list,
                upsert = true
            ) {
                StudentsEntity::id eq studentYearId
            }
    }

    suspend fun removeStudentFromClass(
        studentClassId: Int?
    ) {
        client.postgrest[TABLE_STUDENT_YEAR]
            .delete { StudentYearEntity::id eq studentClassId }
    }

    suspend fun removeClassFromSchoolYears(
        classYearId: Int
    ) {
        client.postgrest[TABLE_CLASS_YEAR]
            .delete { ClassYearEntity::id eq classYearId }
    }

    suspend fun getSubjectList(): List<SubjectEntity> {
        return client.postgrest[TABLE_SUBJECT]
            .select("*")
            .decodeList()
    }

    suspend fun getStudentYears(classYearId: Int): List<StudentsYearsEntity> {
        return client.postgrest[VIEW_STUDENTS]
            .select { StudentsYearsEntity::idClassYear eq classYearId }
            .decodeList()
    }

    suspend fun insertAttendance(
        idClassYear: Int,
        idSubject: Int,
        idTeacher: String,
        information: String,
        latitude: Double?,
        longitude: Double?,
    ): List<AttendanceEntity> {
        return client.postgrest[TABLE_ATTENDANCE]
            .insert(
                AttendanceEntity(
                    idClassYear, idSubject, idTeacher, information, latitude, longitude
                )
            )
            .decodeList()
    }

    suspend fun insertStudentAttendance(
        studentAttendanceList: List<StudentAttendanceEntity>,
        tokenList: List<String?>
    ) {
        val result = client.postgrest[TABLE_STUDENT_ATTENDANCE]
            .insert(
                studentAttendanceList,
                returning = Returning.REPRESENTATION
            )
            .decodeList<StudentAttendanceEntity>()

        if (result.isNotEmpty()) {
            studentAttendanceList.forEachIndexed { index, studentAttendance ->
                if (studentAttendance.status == "Alpha") {
                    val data = JSONObject()
                    data.put("title", "Anak anda dinyatakan ${studentAttendance.status}")
                    data.put("message", "Harap konfirmasi ke pihak sekolah")
                    FCMSender.Builder()
                        .toTokenOrTopic(tokenList[index] ?: return)
                        .setData(data)
                        .responseListener(object: FCMSender.ResponseListener {
                            override fun onFailure(errorCode: Int, message: String) {
                                Log.d("ERROR", "Code: $errorCode, $message")
                            }

                            override fun onSuccess(response: String) {
                                Log.d("SUCCESS", response)
                            }
                        })
                        .serverKey(BuildConfig.FCM_SERVER_KEY)
                        .build()
                        .sendPush(context)
                }
            }
        }
    }

    suspend fun getAttendance(
        studentId: Int
    ): List<AttendanceItemEntity> {
        return client.postgrest[VIEW_ATTENDANCE_ITEM]
            .select {
                AttendanceItemEntity::idStudent eq studentId
            }
            .decodeList()
    }

    @OptIn(SupabaseExperimental::class)
    suspend fun getAttendance(
        studentId: Int,
        currentDay: String,
        nextDay: String
    ): List<AttendanceView> {
        return client.postgrest[VIEW_ATTENDANCE]
            .select {
                AttendanceView::idStudent eq studentId
                gte("created_at", currentDay)
                and {
                    lte("created_at", nextDay)
                }
            }
            .decodeList()
    }

    suspend fun insertGrading(listGradingEntity: List<GradingEntity>) {
        client.postgrest[TABLE_GRADING]
            .insert(
                listGradingEntity,
                upsert = true
            ) {
                GradingEntity::idStudentYear eq listGradingEntity.first().idStudentYear
            }
    }

    suspend fun getGrading(
        studentId: Int,
    ): List<GradeViewEntity> {
        return client.postgrest[VIEW_GRADING]
            .select {
                GradeViewEntity::idStudent eq studentId
            }
            .decodeList()
    }
}