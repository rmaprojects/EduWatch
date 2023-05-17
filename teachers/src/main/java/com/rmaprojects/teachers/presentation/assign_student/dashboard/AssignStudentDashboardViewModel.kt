package com.rmaprojects.teachers.presentation.assign_student.dashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.remote.response.classes.ClassViewEntity
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.teachers.presentation.assign_student.dashboard.event.AssignStudentDashboardEvent
import com.rmaprojects.teachers.presentation.assign_student.dashboard.event.DeleteClassEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignStudentDashboardViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
): ViewModel() {

    private val _uiEvent = MutableSharedFlow<AssignStudentDashboardEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _deleteUiEvent = MutableSharedFlow<DeleteClassEvent>()
    val deleteUiEvent = _deleteUiEvent.asSharedFlow()

    private val _classList = mutableStateOf<List<ClassViewEntity>>(emptyList())
    val classList: State<List<ClassViewEntity>> = _classList

    fun getClassList() {
        viewModelScope.launch {
            _uiEvent.emit(AssignStudentDashboardEvent.Loading)
            try {
                val result = useCases.assignStudentUseCase.getClassYearList()
                _classList.value = result
                _uiEvent.emit(AssignStudentDashboardEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(AssignStudentDashboardEvent.Error(e.message ?: "Error Occurred when Getting Class List"))
            }
        }
    }

    fun deleteClassFromYears(classYearId: Int) {
        viewModelScope.launch {
            _deleteUiEvent.emit(DeleteClassEvent.Loading)
            try {
                useCases.assignStudentUseCase.removeClassFromSchoolYears(classYearId)
                _deleteUiEvent.emit(DeleteClassEvent.Success)
            } catch (e: Exception) {
                _deleteUiEvent.emit(DeleteClassEvent.Error(e.message ?: "Error Occurred when deleting class"))
            }
        }
    }

}