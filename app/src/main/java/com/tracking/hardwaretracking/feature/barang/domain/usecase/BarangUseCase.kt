package com.tracking.hardwaretracking.feature.barang.domain.usecase

import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.repository.BarangRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BarangUseCase @Inject constructor(
    private val barangRepository: BarangRepository
) {
    suspend fun getBarang(userId : Int?) : Flow<BaseResult<List<BarangDomain>, WrappedListResponse<BarangDto>>> {
        return barangRepository.getListBarang(userId)
    }
}