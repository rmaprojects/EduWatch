package com.rmaprojects.teachers.presentation.student_scoring.dashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.remote.response.classes.ClassViewEntity
import com.rmaprojects.core.data.source.remote.response.students.StudentsYearsEntity
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.teachers.presentation.student_scoring.dashboard.event.DashboardScoringEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentScoringDashboardViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
): ViewModel() {

    private val _classIdYearId = mutableStateOf<Int?>(null)
    val classYearId: State<Int?> = _classIdYearId

    private val _getClassYearState = MutableSharedFlow<DashboardScoringEvent>()
    val getClassYearState = _getClassYearState.asSharedFlow()

    private val _classYearList = mutableStateOf<List<ClassViewEntity>>(emptyList())
    val classYearList: State<List<ClassViewEntity>> = _classYearList

    private val _studentList = mutableStateListOf<StudentsYearsEntity>()
    val studentList = _studentList

    private val _uiState = MutableSharedFlow<DashboardScoringEvent>()
    val uiState = _uiState.asSharedFlow()

    fun getStudentListByClassYear(classYearId: Int) {
        viewModelScope.launch {
            _uiState.emit(DashboardScoringEvent.Loading)
            try {
                val result = useCases.attendanceUseCase.getStudentByClassList(classYearId)
                _studentList.clear()
                _studentList.addAll(result)
                _uiState.emit(DashboardScoringEvent.Success)
            } catch (e: Exception) {
                _uiState.emit(DashboardScoringEvent.Error(e.message ?: "Error when getting Student list"))
            }
        }
    }

    fun setClassYearId(newClassId: Int) {
        _classIdYearId.value = newClassId
    }

    fun getClassYear() {
        viewModelScope.launch {
            _getClassYearState.emit(DashboardScoringEvent.Loading)
            try {
                val result = useCases.assignStudentUseCase.getClassYearList()
                _classYearList.value = result
                _getClassYearState.emit(DashboardScoringEvent.Success)
            } catch (e: Exception) {
                _getClassYearState.emit(DashboardScoringEvent.Error(e.message ?: "Error when getting classyear"))
            }
        }
    }

}