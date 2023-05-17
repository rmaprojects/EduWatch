package com.rmaprojects.parents.presentation.scoring

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.local.LocalUser
import com.rmaprojects.core.data.source.remote.response.grades.GradeViewEntity
import com.rmaprojects.core.domain.model.ClassModel
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.parents.presentation.scoring.event.ScoringUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoringViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
) : ViewModel() {

    private val _pasUiEvent = MutableSharedFlow<ScoringUiEvent>()
    val pasUiEvent = _pasUiEvent.asSharedFlow()

    private val _ptsUiEvent = MutableSharedFlow<ScoringUiEvent>()
    val ptsUiEvent = _ptsUiEvent.asSharedFlow()

    private val _fetchGradeUiEvent = MutableSharedFlow<ScoringUiEvent>()
    val fetchGradeUiEvent = _fetchGradeUiEvent.asSharedFlow()

    private val _pasGradesList = mutableStateListOf<GradeViewEntity>()
    val pasGradesList = _pasGradesList

    private val _ptsGradesList = mutableStateListOf<GradeViewEntity>()
    val ptsGradesList = _ptsGradesList

    private val _grades = mutableStateListOf<GradeViewEntity>()

    private val _classYearId = mutableStateOf<Int?>(null)
    private val _classYearIdList = mutableStateListOf<ClassModel>()
    val classYearIdList = _classYearIdList

    fun fetchGrades() {
        viewModelScope.launch {
            _fetchGradeUiEvent.emit(ScoringUiEvent.Loading)
            try {
                val localStudentId = LocalUser.studentId
                if (!localStudentId.isNullOrBlank()) {

                    val grades = useCases.gradingUseCase.getGrading(
                        localStudentId.toInt()
                    )
                    if (grades.isEmpty()) {
                        _fetchGradeUiEvent.emit(ScoringUiEvent.EmptyList)
                        return@launch
                    }

                    _classYearIdList.clear()
                    _classYearIdList.addAll(
                        grades.getOnlyClassYearIdList().map {
                            ClassModel(
                                classId = it.idClassYear,
                                classGrade = it.classX,
                                classVocation = it.vocation,
                                classYear = it.year
                            )
                        }
                    )
                    _grades.clear()
                    _grades.addAll(grades)
                    _fetchGradeUiEvent.emit(ScoringUiEvent.Success)
                }
            } catch (e: Exception) {
                _fetchGradeUiEvent.emit(ScoringUiEvent.Error(e.message ?: "Error when fetching grades"))
            }
        }
    }

    fun fetchPasGrades() {
        viewModelScope.launch {
            _pasUiEvent.emit(ScoringUiEvent.Loading)
            try {
                if (_grades.isEmpty()) {
                    _ptsUiEvent.emit(ScoringUiEvent.EmptyList)
                    return@launch
                }
                _pasGradesList.clear()
                _pasGradesList.addAll(
                    _grades.filter { it.type == "PAS" && it.idClassYear == _classYearId.value }
                )
                _pasUiEvent.emit(ScoringUiEvent.Success)
            } catch (e: Exception) {
                _pasUiEvent.emit(ScoringUiEvent.Error(e.message ?: "Error when filtering"))
            }
        }
    }

    fun fetchPtsGrades() {
        viewModelScope.launch {
            _ptsUiEvent.emit(ScoringUiEvent.Loading)
            try {
                if (_grades.isEmpty()) {
                    _ptsUiEvent.emit(ScoringUiEvent.EmptyList)
                    return@launch
                }
                _ptsGradesList.clear()
                _ptsGradesList.addAll(
                    _grades.filter { it.type == "PTS" && it.idClassYear == _classYearId.value }
                )
                _ptsUiEvent.emit(ScoringUiEvent.Success)
            } catch (e: Exception) {
                _ptsUiEvent.emit(ScoringUiEvent.Error(e.message ?: "Error when filtering"))
            }
        }
    }

    fun setClassYearId(newClassId: Int) {
        _classYearId.value = newClassId
    }

    private fun List<GradeViewEntity>.getOnlyClassYearIdList(): List<GradeViewEntity> {
        val list = mutableListOf<GradeViewEntity>()
        for (i in this.indices) {
            if (list.any { it.idClassYear == this[i].idClassYear }) {
                list.add(this[i])
            }
        }
        return list
    }

}