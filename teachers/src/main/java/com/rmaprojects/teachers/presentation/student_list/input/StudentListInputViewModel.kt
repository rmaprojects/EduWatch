package com.rmaprojects.teachers.presentation.student_list.input

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.remote.response.students.StudentsEntity
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.teachers.presentation.student_list.input.events.StudentInputEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListInputViewModel @Inject constructor(
    private val useCases: EduWatchUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val studentId: Int? = savedStateHandle[StudentListInputFragment.STUDENT_ID]

    private val _studentDetail = mutableStateOf<StudentsEntity?>(null)
    val studentDetail = _studentDetail.value

    private val _uiEvent = MutableSharedFlow<StudentInputEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun getStudentDetail() {
        viewModelScope.launch {
            val studentDetail = useCases.insertStudentUseCases.getStudentDetail(studentId)
            _studentDetail.value = studentDetail
        }
    }

    fun insertStudent(
        name: String,
        nis: Int,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            _uiEvent.emit(StudentInputEvent.Loading)
            try {
                useCases.insertStudentUseCases.insertStudent(
                    studentId,
                    name,
                    nis,
                    phoneNumber
                )
                _uiEvent.emit(StudentInputEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(StudentInputEvent.Error(e.message ?: "Error Occurred when Inserting Student"))
            }
        }
    }
}