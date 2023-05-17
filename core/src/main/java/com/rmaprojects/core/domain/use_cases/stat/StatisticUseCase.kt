package com.rmaprojects.core.domain.use_cases.stat

import com.rmaprojects.core.data.source.remote.response.attendance.AttendanceItemEntity
import com.rmaprojects.core.data.source.remote.response.attendance.AttendanceView
import com.rmaprojects.core.domain.repository.EduWatchRepository

class StatisticUseCase(
    private val repository: EduWatchRepository
) {
    suspend fun getAttendance(studentId: Int): List<AttendanceItemEntity> {
        return repository.getAttendance(studentId)
    }

    suspend fun getAttendance(
        studentId: Int,
        today: String,
        nextDay: String
    ): List<AttendanceView> {
        return repository.getAttendance(studentId, today, nextDay)
    }
}