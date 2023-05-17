package com.rmaprojects.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.auth.event.AuthUiEvent
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
): ViewModel() {

    private val _uiEvent = MutableSharedFlow<AuthUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun login(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _uiEvent.emit(AuthUiEvent.EmptyTextField)
                return@launch
            }
            try {
                useCases.authUseCases.login(email, password)
                _uiEvent.emit(AuthUiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(AuthUiEvent.Error(e.message ?: "Error Occurred when Logging in."))
            }
        }
    }

}