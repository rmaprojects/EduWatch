package com.rmaprojects.teachers.presentation.attendance

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.local.LocalUser
import com.rmaprojects.core.data.source.remote.response.classes.ClassViewEntity
import com.rmaprojects.core.data.source.remote.response.subject.SubjectEntity
import com.rmaprojects.core.domain.model.StudentAttendanceModel
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.teachers.presentation.attendance.event.AttendanceEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
) : ViewModel() {

    private val _getClassYearEvent = MutableSharedFlow<AttendanceEvent>()
    val getClassYearEvent = _getClassYearEvent.asSharedFlow()

    private val _getSubjectEvent = MutableSharedFlow<AttendanceEvent>()
    val getSubjectEvent = _getSubjectEvent.asSharedFlow()

    private val _getStudentEvent = MutableSharedFlow<AttendanceEvent>()
    val getStudentEvent = _getStudentEvent.asSharedFlow()

    private val _uiEvent = MutableSharedFlow<AttendanceEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _classYearList = mutableStateOf<List<ClassViewEntity>>(emptyList())
    val classYearList: State<List<ClassViewEntity>> = _classYearList

    private val _subjectList = mutableStateOf<List<SubjectEntity>>(emptyList())
    val subjectList: State<List<SubjectEntity>> = _subjectList

    private val _studentList = mutableStateListOf<StudentAttendanceModel>()
    val studentList = _studentList

    private val _tokenList = mutableStateListOf<String?>()

    private val _selectedSubjectId = mutableStateOf<Int?>(null)
    private val _selectedClassYearId = mutableStateOf<Int?>(null)
    private val _topicToday = mutableStateOf("")


    fun setSelectedSubject(newSubjectId: Int) {
        _selectedSubjectId.value = newSubjectId
    }

    fun setSelectedClassYear(newClassYear: Int) {
        _selectedClassYearId.value = newClassYear
    }

    fun setTopic(newTopic: String) {
        _topicToday.value = newTopic
    }

    fun submitAttendance(
        longitude: Double?,
        latitude: Double?
    ) {
        viewModelScope.launch {
            _uiEvent.emit(AttendanceEvent.Loading)
            try {
                val subjectId = _selectedSubjectId.value
                val classYearId = _selectedClassYearId.value
                val topic = _topicToday.value
                if (subjectId == null || classYearId == null || topic.isBlank()) return@launch

                val insertAttendanceResult = useCases.attendanceUseCase.insertAttendance(
                    idClassYear = classYearId,
                    idSubject = subjectId,
                    idTeacher = LocalUser.id!!,
                    information = topic,
                    latitude = latitude,
                    longitude = longitude
                )
                val idAttendance = insertAttendanceResult.first().id
                if (idAttendance != null) {
                    useCases.attendanceUseCase.insertStudentAttendance(
                        idAttendance = idAttendance,
                        _studentList.toList(),
                        _tokenList
                    )
                }
                _uiEvent.emit(AttendanceEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(AttendanceEvent.Error(e.message ?: "Error when inserting attendance"))
            }
        }
    }

    fun getClassYearList() {
        viewModelScope.launch {
            _getClassYearEvent.emit(AttendanceEvent.Loading)
            try {
                val result = useCases.assignStudentUseCase.getClassYearList()
                _classYearList.value = result
                _getClassYearEvent.emit(AttendanceEvent.Success)
            } catch (e: Exception) {
                _getClassYearEvent.emit(
                    AttendanceEvent.Error(
                        e.message ?: "Error when getting class list"
                    )
                )
            }
        }
    }

    fun getSubjectList() {
        viewModelScope.launch {
            _getSubjectEvent.emit(AttendanceEvent.Loading)
            try {
                val result = useCases.attendanceUseCase.getSubjectList()
                _subjectList.value = result
                _getSubjectEvent.emit(AttendanceEvent.Success)
            } catch (e: Exception) {
                _getSubjectEvent.emit(
                    AttendanceEvent.Error(
                        e.message ?: "Error when getting subject list"
                    )
                )
            }
        }
    }

    fun getStudentList() {
        viewModelScope.launch {
            _getStudentEvent.emit(AttendanceEvent.Loading)
            try {
                val classYearId = _selectedClassYearId.value
                if (classYearId != null) {
                    val result = useCases.attendanceUseCase.getStudentByClassList(classYearId)
                    _studentList.clear()
                    _studentList.addAll(
                        result.map {
                            StudentAttendanceModel(
                                studentName = it.nameStudent,
                                studentNis = it.nis,
                                studentStatus = "Hadir",
                                studentYearId = it.id!!
                            )
                        }
                    )
                    _tokenList.clear()
                    _tokenList.addAll(
                        result.map { it.token }
                    )
                }
                _getStudentEvent.emit(AttendanceEvent.Success)
            } catch (e: Exception) {
                _getStudentEvent.emit(
                    AttendanceEvent.Error(
                        e.message ?: "Error when getting student list"
                    )
                )
            }
        }
    }

    fun changeStudentStatus(
        studentYearId: Int,
        newStatus: String
    ) {
        _studentList.find {
            it.studentYearId == studentYearId
        }?.studentStatus = newStatus
    }
}