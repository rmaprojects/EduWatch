package com.rmaprojects.teachers.presentation.student_list.dashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.remote.response.students.StudentsEntity
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.teachers.presentation.student_list.dashboard.events.DeleteStudentEvent
import com.rmaprojects.teachers.presentation.student_list.dashboard.events.StudentListDashboardUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListDashboardViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
): ViewModel() {

    private val _studentList = mutableStateOf<List<StudentsEntity>>(emptyList())
    val studentList: State<List<StudentsEntity>> = _studentList

    private val _uiEvent = MutableSharedFlow<StudentListDashboardUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _deleteUiEvent = MutableSharedFlow<DeleteStudentEvent>()
    val deleteUiEvent = _deleteUiEvent.asSharedFlow()

    fun getStudents() {
        viewModelScope.launch {
            _uiEvent.emit(StudentListDashboardUiEvent.Loading)
            try {
                val result = useCases.insertStudentUseCases.getStudents()
                _studentList.value = result
                _uiEvent.emit(StudentListDashboardUiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(StudentListDashboardUiEvent.Error(e.message ?: "Error Occurred when Getting Student List"))
            }
        }
    }

    fun deleteStudent(studentId: Int?) {
        viewModelScope.launch {
            _deleteUiEvent.emit(DeleteStudentEvent.Loading)
            try {
                useCases.insertStudentUseCases.deleteStudent(studentId)
                _deleteUiEvent.emit(DeleteStudentEvent.Success)
            } catch (e: Exception) {
                _deleteUiEvent.emit(DeleteStudentEvent.Error(e.message ?: "Error Occurred when Deleting Student"))
            }
        }
    }
}