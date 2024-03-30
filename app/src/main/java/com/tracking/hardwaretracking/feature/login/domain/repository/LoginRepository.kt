package com.tracking.hardwaretracking.feature.login.domain.repository

import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.login.data.dto.LoginDto
import com.tracking.hardwaretracking.feature.login.data.dto.UserDto
import com.tracking.hardwaretracking.feature.login.domain.model.LoginDomain
import com.tracking.hardwaretracking.feature.login.domain.model.UserDomain
import com.tracking.hardwaretracking.feature.login.domain.request.LoginRequest
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun postLogin(loginRequest: LoginRequest) : Flow<BaseResult<LoginDomain, WrappedResponse<LoginDto>>>
    suspend fun getListUser() : Flow<BaseResult<List<UserDomain>, WrappedListResponse<UserDto>>>
}