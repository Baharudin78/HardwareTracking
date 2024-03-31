package com.tracking.hardwaretracking.feature.barang.domain.usecase

import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.repository.BarangRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDetailBarangUsecase @Inject constructor(
    private val barangRepository: BarangRepository
) {
    suspend fun getDetailBarang(qrCode : String) : Flow<BaseResult<BarangDomain, WrappedResponse<BarangDto>>> {
        return barangRepository.getDetailBarang(qrCode)
    }
}