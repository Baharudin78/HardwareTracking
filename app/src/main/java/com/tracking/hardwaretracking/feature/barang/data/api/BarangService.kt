package com.tracking.hardwaretracking.feature.barang.data.api

import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.domain.request.UpdateBarangRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface BarangService {

    @GET("services/barang")
    suspend fun getListBarang() : Response<WrappedListResponse<BarangDto>>

    @PUT("services/barang")
    suspend fun updateBarang(
        @Query("id") id : String,
        @Body request : UpdateBarangRequest
    ) : Response<WrappedResponse<BarangDto>>
}