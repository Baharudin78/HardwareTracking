package com.tracking.hardwaretracking.feature.barang.domain.usecase

import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.LogDto
import com.tracking.hardwaretracking.feature.barang.domain.model.LogDomain
import com.tracking.hardwaretracking.feature.barang.domain.repository.BarangRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLogUseCase @Inject constructor(
    private val barangRepository: BarangRepository
) {
    suspend fun getLog(): Flow<BaseResult<List<LogDomain>, WrappedListResponse<LogDto>>> {
        return barangRepository.getLog()
    }
}