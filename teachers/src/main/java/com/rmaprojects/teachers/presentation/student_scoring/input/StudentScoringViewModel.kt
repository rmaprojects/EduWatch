package com.rmaprojects.teachers.presentation.student_scoring.input

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.domain.model.GradeInput
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.teachers.presentation.student_scoring.input.event.InputScoringEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StudentScoringViewModel @Inject constructor(
    private val useCases: EduWatchUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val studentYearId: Int =
        savedStateHandle[StudentScoringInputFragment.STUDENT_YEAR_ID]
            ?: throw Exception("studentYearId Required")

    private val _gradingList = mutableStateListOf<GradeInput>()
    val gradingList = _gradingList

    private val _uiEvent = MutableSharedFlow<GradeInput>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _getSubjectEvent = MutableSharedFlow<InputScoringEvent>()
    val getSubjectEvent = _getSubjectEvent.asSharedFlow()

    private val _gradeType = mutableStateOf("PTS")

    fun setGradingType(newType: String) {
        _gradeType.value = newType
    }

    private fun getAllGradingItems() {
        viewModelScope.launch {
            _getSubjectEvent.emit(InputScoringEvent.Loading)
            try {
                val subjectList = useCases.attendanceUseCase.getSubjectList()

                val gradeList = subjectList.map {
                    GradeInput(
                        subjectName = it.subjectName,
                        subjectId = it.id,
                        studentYearId = studentYearId,
                        gradeType = _gradeType.value
                    )
                }

                _gradingList.clear()
                _gradingList.addAll(gradeList)

                _getSubjectEvent.emit(InputScoringEvent.Success)
            } catch (e: Exception) {
                _getSubjectEvent.emit(
                    InputScoringEvent.Error(
                        e.message ?: "Error when getting subjects"
                    )
                )
            }
        }
    }

    fun changeGradeInput(subjectId: Int, grade: String) {
        if (grade.isEmpty()) return
        _gradingList.find {
            it.subjectId == subjectId
        }?.grade = grade.toInt()
    }

    fun submit() {
        viewModelScope.launch {
            useCases.gradingUseCase.insertGrading(
                listGrading = _gradingList,
                gradingType = _gradeType.value
            )
        }
    }

    init {
        getAllGradingItems()
    }
}