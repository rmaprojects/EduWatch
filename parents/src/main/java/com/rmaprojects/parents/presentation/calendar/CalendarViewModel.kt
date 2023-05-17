package com.rmaprojects.parents.presentation.calendar

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.local.LocalUser
import com.rmaprojects.core.data.source.remote.response.attendance.AttendanceView
import com.rmaprojects.core.domain.model.AttendanceTimeline
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.parents.presentation.calendar.event.CalendarUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
): ViewModel() {


    private val _uiState = MutableSharedFlow<CalendarUiEvent>()
    val uiState = _uiState.asSharedFlow()

    private val _attendanceList = mutableStateListOf<AttendanceView>()
    val attendanceList = mutableStateListOf<AttendanceTimeline>()

    private val _selectedDate = mutableStateOf<Date?>(null)

    fun getTimeLine() {
        viewModelScope.launch {
            _uiState.emit(CalendarUiEvent.Loading)
            try {
                val localStudentId = LocalUser.studentId
                val date = _selectedDate.value
                if (localStudentId != null && date != null) {
                    val result = useCases.statisticUseCase.getAttendance(
                        studentId = localStudentId.toInt(),
                        date.dateNow,
                        date.dateNext
                    )

                    if (result.isEmpty()) {
                        _uiState.emit(CalendarUiEvent.EmptyTimeline)
                    }

                    attendanceList.clear()

                    attendanceList.addAll(
                        result.map {
                            AttendanceTimeline(
                                it.nameSubject,
                                it.information,
                                it.nameTeacher,
                                clock = it.createdAt.substringBefore("T", "").substringAfter("+", ""),
                            )
                        }
                    )
                    _attendanceList.clear()
                    _attendanceList.addAll(result)
                }
                _uiState.emit(CalendarUiEvent.Success)
            } catch (e: Exception) {
                _uiState.emit(CalendarUiEvent.Error(e.message ?: "Error when getting timeline"))
            }
        }
    }

    fun setSelectedDate(newDate: Date) {
        _selectedDate.value = newDate
    }
}

data class Date(
    val dateNow: String,
    val dateNext: String
)