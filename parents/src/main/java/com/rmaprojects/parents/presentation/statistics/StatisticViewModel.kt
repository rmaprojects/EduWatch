package com.rmaprojects.parents.presentation.statistics

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.local.LocalUser
import com.rmaprojects.core.data.source.remote.response.attendance.AttendanceItemEntity
import com.rmaprojects.core.domain.model.AttendanceTimeline
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.parents.presentation.statistics.event.StatisticEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
) : ViewModel() {

    private val _uiState = MutableSharedFlow<StatisticEvents>()
    val uiState = _uiState.asSharedFlow()

    private val _attendanceList = mutableStateOf<List<AttendanceItemEntity>>(emptyList())
    val attendanceList: State<List<AttendanceItemEntity>> = _attendanceList

    private val _attendanceTimelineList = mutableListOf<AttendanceTimeline>()
    val attendanceTimelineList = _attendanceTimelineList

    fun fetchAttendances() {
        viewModelScope.launch {
            _uiState.emit(StatisticEvents.Loading)
            getStudentAttendance()
            getAttendance()
            _uiState.emit(StatisticEvents.Success)
        }
    }

    private suspend fun getStudentAttendance() {
        try {
            val studentId = LocalUser.studentId
            if (studentId != null) {
                val result = useCases.statisticUseCase.getAttendance(studentId.toInt())
                _attendanceList.value = result
            }
        } catch (e: Exception) {
            _uiState.emit(StatisticEvents.Error(e.message ?: "Error when getting Stat"))
        }
    }

    private suspend fun getAttendance() {
        try {
            val studentId = LocalUser.studentId

            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DATE, 1)

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date().time)
            val nextDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            if (studentId != null) {
                val result =
                    useCases.statisticUseCase.getAttendance(studentId.toInt(), today, nextDay)
                _attendanceTimelineList.clear()
                _attendanceTimelineList.addAll(
                    result.map { attendance ->
                        AttendanceTimeline(
                            subjectName = attendance.nameSubject,
                            teacherName = attendance.nameTeacher,
                            clock = attendance.createdAt.substringBefore("T", "")
                                .substringAfter("+", ""),
                            subjectTopic = attendance.information
                        )
                    }
                )
            }
        } catch (e: Exception) {
            _uiState.emit(StatisticEvents.Error(e.message ?: "Error when getting Attendance"))
        }
    }

}