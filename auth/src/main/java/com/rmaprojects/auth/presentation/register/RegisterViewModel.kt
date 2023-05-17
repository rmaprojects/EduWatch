package com.rmaprojects.auth.presentation.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.auth.event.AuthUiEvent
import com.rmaprojects.auth.event.FetchStudentUiEvent
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.core.utils.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<AuthUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _fetchStudentUiEvent = MutableSharedFlow<FetchStudentUiEvent>()
    val fetchStudentUiEvent = _fetchStudentUiEvent.asSharedFlow()

    private val _userRole = mutableStateOf(UserRole.PARENTS)

    private val _selectedStudentId = mutableStateOf<Int?>(null)

    init {
        getStudents()
    }

    fun setUserRole(role: Int) {
        _userRole.value = when (role) {
            0 -> UserRole.TEACHERS
            1 -> UserRole.PARENTS
            else -> throw Exception("Unknown Value")
        }
    }

    fun setSelectedStudentId(studentId: Int?) {
        _selectedStudentId.value = studentId
    }

    fun register(
        email: String,
        name: String,
        password: String,
        phoneNumber: String,
    ) {
        viewModelScope.launch {
            if (_userRole.value == UserRole.PARENTS) {
                if (phoneNumber.isBlank() || _selectedStudentId.value == null) {
                    _uiEvent.emit(AuthUiEvent.EmptyTextField)
                    return@launch
                }
            }

            if (email.isBlank() || password.isBlank() || name.isBlank()) {
                _uiEvent.emit(AuthUiEvent.EmptyTextField)
                return@launch
            }
            _uiEvent.emit(AuthUiEvent.Loading)
            try {
                useCases.authUseCases.register(
                    email, password, name, _userRole.value, phoneNumber, _selectedStudentId.value
                )
                _uiEvent.emit(AuthUiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(
                    AuthUiEvent.Error(
                        e.message ?: "Error Occurred when Registering User"
                    )
                )
            }
        }
    }

    private fun getStudents() {
        viewModelScope.launch {
            try {
                val result = useCases.authUseCases.getStudents()
                _fetchStudentUiEvent.emit(FetchStudentUiEvent.Success(result))
            } catch (e: Exception) {
                _fetchStudentUiEvent.emit(
                    FetchStudentUiEvent.Error(
                        e.message ?: "Error Occurred when getting students"
                    )
                )
            }
        }
    }
}