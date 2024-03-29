package com.tracking.hardwaretracking.feature.barang.domain.repository

import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import kotlinx.coroutines.flow.Flow

interface BarangRepository {
    suspend fun getListBarang() : Flow<BaseResult<List<BarangDomain>, WrappedListResponse<BarangDto>>>
}