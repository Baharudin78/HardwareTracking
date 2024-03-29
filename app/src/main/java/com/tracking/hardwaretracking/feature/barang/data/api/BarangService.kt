package com.tracking.hardwaretracking.feature.barang.data.api

import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import retrofit2.Response
import retrofit2.http.GET

interface BarangService {

    @GET("services/barang")
    suspend fun getListBarang() : Response<WrappedListResponse<BarangDto>>
}