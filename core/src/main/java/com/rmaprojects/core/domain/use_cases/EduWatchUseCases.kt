package com.rmaprojects.core.domain.use_cases

import com.rmaprojects.core.domain.use_cases.assign_student.AssignStudentUseCase
import com.rmaprojects.core.domain.use_cases.attendance.AttendanceUseCase
import com.rmaprojects.core.domain.use_cases.auth.AuthUseCases
import com.rmaprojects.core.domain.use_cases.grading.GradingUseCase
import com.rmaprojects.core.domain.use_cases.insert_student.InsertStudentUseCases
import com.rmaprojects.core.domain.use_cases.profile.ProfileUseCase
import com.rmaprojects.core.domain.use_cases.stat.StatisticUseCase

interface EduWatchUseCases {
    val authUseCases: AuthUseCases
    val insertStudentUseCases: InsertStudentUseCases
    val assignStudentUseCase: AssignStudentUseCase
    val attendanceUseCase: AttendanceUseCase
    val statisticUseCase: StatisticUseCase
    val profileUseCase: ProfileUseCase
    val gradingUseCase: GradingUseCase
}