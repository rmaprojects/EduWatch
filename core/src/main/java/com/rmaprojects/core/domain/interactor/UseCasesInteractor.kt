package com.rmaprojects.core.domain.interactor

import com.rmaprojects.core.domain.repository.EduWatchRepository
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.core.domain.use_cases.assign_student.AssignStudentUseCase
import com.rmaprojects.core.domain.use_cases.attendance.AttendanceUseCase
import com.rmaprojects.core.domain.use_cases.auth.AuthUseCases
import com.rmaprojects.core.domain.use_cases.grading.GradingUseCase
import com.rmaprojects.core.domain.use_cases.insert_student.InsertStudentUseCases
import com.rmaprojects.core.domain.use_cases.profile.ProfileUseCase
import com.rmaprojects.core.domain.use_cases.stat.StatisticUseCase
import javax.inject.Inject

class UseCasesInteractor @Inject constructor(
    private val repository: EduWatchRepository
) : EduWatchUseCases {
    override val authUseCases: AuthUseCases
        get() = AuthUseCases(repository)
    override val insertStudentUseCases: InsertStudentUseCases
        get() = InsertStudentUseCases(repository)
    override val assignStudentUseCase: AssignStudentUseCase
        get() = AssignStudentUseCase(repository)
    override val attendanceUseCase: AttendanceUseCase
        get() = AttendanceUseCase(repository)
    override val statisticUseCase: StatisticUseCase
        get() = StatisticUseCase(repository)
    override val profileUseCase: ProfileUseCase
        get() = ProfileUseCase(repository)
    override val gradingUseCase: GradingUseCase
        get() = GradingUseCase(repository)
}