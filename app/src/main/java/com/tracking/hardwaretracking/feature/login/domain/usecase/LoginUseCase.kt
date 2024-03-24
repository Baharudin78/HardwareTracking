package com.tracking.hardwaretracking.feature.login.domain.usecase

import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.login.data.dto.LoginDto
import com.tracking.hardwaretracking.feature.login.domain.model.LoginDomain
import com.tracking.hardwaretracking.feature.login.domain.repository.LoginRepository
import com.tracking.hardwaretracking.feature.login.domain.request.LoginRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend fun invoke(loginRequest: LoginRequest): Flow<BaseResult<LoginDomain, WrappedResponse<LoginDto>>> {
        return loginRepository.postLogin(loginRequest)
    }
}