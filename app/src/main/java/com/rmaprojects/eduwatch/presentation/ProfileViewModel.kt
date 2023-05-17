package com.rmaprojects.eduwatch.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmaprojects.core.data.source.local.LocalUser
import com.rmaprojects.core.data.source.remote.response.parents.ParentsEntity
import com.rmaprojects.core.data.source.remote.response.teacher.TeachersEntity
import com.rmaprojects.core.domain.model.UserData
import com.rmaprojects.core.domain.use_cases.EduWatchUseCases
import com.rmaprojects.core.utils.UserRole
import com.rmaprojects.eduwatch.presentation.event.ProfileUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val useCases: EduWatchUseCases
): ViewModel() {

    private val _userData = mutableStateOf(UserData("", "", ""))
    val userData: State<UserData> = _userData

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun logOut() {
        viewModelScope.launch { useCases.authUseCases.logOut() }
    }

    fun getUserDetail() {
        viewModelScope.launch {
            _uiEvent.emit(ProfileUiEvent.Success)
            try {
                val id = LocalUser.id
                val role = LocalUser.role

                if (id.isNullOrBlank() || role == UserRole.NONE) {
                    return@launch
                }

                val userData = useCases.profileUseCase.getUserData(
                    userId = id,
                    userRole = role
                ).first()

                when (userData) {
                    is TeachersEntity -> {
                        _userData.value = UserData(
                            userData.name,
                            id,
                            role.roleValue
                        )
                    }
                    is ParentsEntity -> {
                        _userData.value = UserData(
                            userData.nameParents,
                            id,
                            role.roleValue
                        )
                    }
                }
                _uiEvent.emit(ProfileUiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.emit(ProfileUiEvent.Error(e.message ?: "Error when getting Profile"))
            }
        }
    }
}