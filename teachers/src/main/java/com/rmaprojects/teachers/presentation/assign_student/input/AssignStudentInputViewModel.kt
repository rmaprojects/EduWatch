package com.rmaprojects.teachers.presentation.assign_student.input

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.remote.response.students.StudentsEntity
import com.rmaprojects.core.domain.model.ChosenStudentModel
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.teachers.presentation.assign_student.input.events.AssignStudentInputEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignStudentInputViewModel @Inject constructor(
    private val useCases: EduWatchUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val classYearId: Int? = savedStateHandle[AssignStudentInputFragment.CLASS_YEAR]

    private val _studentList = mutableStateOf<List<StudentsEntity>>(emptyList())
    val studentList: State<List<StudentsEntity>> = _studentList

    private val _uiEvent = MutableSharedFlow<AssignStudentInputEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _deleteStudentEvent = MutableSharedFlow<AssignStudentInputEvent>()
    val deleteStudentEvent = _deleteStudentEvent.asSharedFlow()

    private val _getStudentEvent = MutableSharedFlow<AssignStudentInputEvent>()
    val getStudentEvent = _getStudentEvent.asSharedFlow()

    private val _chosenStudent = mutableListOf<ChosenStudentModel>()
    val chosenStudent: List<ChosenStudentModel> = _chosenStudent.toList()

    var applyStudentDetail: (
        (
        grade: Int,
        vocation: String,
        year: Int
    ) -> Unit)? = null

    suspend fun getClassYear(classYearId: Int) {
        try {
            val result = useCases.assignStudentUseCase.getClassYearList(classYearId).first()
            applyStudentDetail?.invoke(result.grade, result.vocation, result.year)
        } catch (e: Exception) {
            Log.d("GET_CLASS_YEAR", e.toString())
        }
    }

    fun chooseStudent(
        studentName: String,
        nis: Int,
        studentId: Int,
        studentYearId: Int? = null
    ) {
        _chosenStudent.add(
            ChosenStudentModel(
                studentName,
                nis,
                studentId,
                studentYearId
            )
        )
    }

    private fun insertStudent(
        grade: Int,
        year: Int,
        vocation: String
    ) {
        viewModelScope.launch {
            _uiEvent.emit(AssignStudentInputEvent.Loading)
            try {
                val insertClass = useCases.assignStudentUseCase.insertClass(
                    grade, vocation
                )
                val insertClassYearResult = useCases.assignStudentUseCase.insertClasYear(
                    year,
                    insertClass.first().idClass
                )
                val classYear = insertClassYearResult.first().id
                val studentYearId = _chosenStudent.first().studentYearId
                val studentIds = _chosenStudent.map { it.studentId }

                if (classYear != null) {
                    useCases.assignStudentUseCase.assignStudentWithSchoolYear(
                        studentYearId = studentYearId,
                        studentId = studentIds,
                        classYearId = classYear
                    )
                }
                _uiEvent.emit(AssignStudentInputEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(
                    AssignStudentInputEvent.Error(
                        e.message ?: "Error when inserting student"
                    )
                )
            }
        }
    }

    private fun insertStudent() {
        viewModelScope.launch {
            _uiEvent.emit(AssignStudentInputEvent.Loading)
            try {
                val studentYearId = _chosenStudent.first().studentYearId
                val studentIds = _chosenStudent.map { it.studentId }

                if (classYearId != null) {
                    useCases.assignStudentUseCase.assignStudentWithSchoolYear(
                        studentYearId = studentYearId,
                        studentId = studentIds,
                        classYearId = classYearId
                    )
                }
                _uiEvent.emit(AssignStudentInputEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(
                    AssignStudentInputEvent.Error(
                        e.message ?: "Error when inserting student"
                    )
                )
            }
        }
    }

    fun submit(
        grade: Int,
        year: Int,
        vocation: String
    ) {
        if (classYearId == null) {
            insertStudent(grade, year, vocation)
        } else {
            insertStudent()
        }
    }

    fun deleteStudent(
        studentYearId: Int
    ) {
        if (classYearId != null) {
            viewModelScope.launch {
                _deleteStudentEvent.emit(AssignStudentInputEvent.Loading)
                try {
                    useCases.assignStudentUseCase.removeStudentFromClass(studentYearId)
                    _deleteStudentEvent.emit(AssignStudentInputEvent.Success)
                } catch (e: Exception) {
                    _deleteStudentEvent.emit(
                        AssignStudentInputEvent.Error(
                            e.message ?: "Error when deleting student"
                        )
                    )
                }
            }
        } else {
            _chosenStudent.removeIf { it.studentYearId == studentYearId }
        }
    }
}