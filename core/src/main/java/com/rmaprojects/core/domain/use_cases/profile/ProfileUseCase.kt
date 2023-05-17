package com.rmaprojects.core.domain.use_cases.profile

import com.rmaprojects.core.domain.repository.EduWatchRepository
import com.rmaprojects.core.utils.UserRole

class ProfileUseCase(
    private val repository: EduWatchRepository
) {
    suspend fun getUserData(
        userId: String,
        userRole: UserRole
    ): List<Any> {
        return repository.getUserDetail(userId, userRole)
    }
}