package com.tracking.hardwaretracking.feature.barang.domain.usecase

import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.feature.login.data.dto.UserDto
import com.tracking.hardwaretracking.feature.login.domain.model.UserDomain
import com.tracking.hardwaretracking.feature.login.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUsecase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend fun getUser() : Flow<BaseResult<List<UserDomain>, WrappedListResponse<UserDto>>> {
        return loginRepository.getListUser()
    }
}