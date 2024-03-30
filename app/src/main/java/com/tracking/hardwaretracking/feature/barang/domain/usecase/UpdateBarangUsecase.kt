package com.tracking.hardwaretracking.feature.barang.domain.usecase

import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.repository.BarangRepository
import com.tracking.hardwaretracking.feature.barang.domain.request.UpdateBarangRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateBarangUsecase @Inject constructor(
    private val barangRepository: BarangRepository
) {
    suspend fun updateBarang(id : String, request : UpdateBarangRequest) : Flow<BaseResult<BarangDomain, WrappedResponse<BarangDto>>> {
        return barangRepository.updateBarang(id, request)
    }
}