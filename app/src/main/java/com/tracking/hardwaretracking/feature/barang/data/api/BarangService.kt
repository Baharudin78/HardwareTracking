package com.tracking.hardwaretracking.feature.barang.data.api

import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.data.dto.LogDto
import com.tracking.hardwaretracking.feature.barang.domain.request.UpdateBarangRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface BarangService {

    @GET("services/barang")
    suspend fun getListBarang(
        @Query("responsible_person_id") userId : Int? = null
    ) : Response<WrappedListResponse<BarangDto>>

    @PUT("services/barang")
    suspend fun updateBarang(
        @Query("id") id : Int,
        @Body request : UpdateBarangRequest
    ) : Response<WrappedResponse<BarangDto>>

    @GET("services/barang")
    suspend fun getDetailBarang(
        @Query("qrcode") qrcode : String
    ) : Response<WrappedResponse<BarangDto>>

    @GET("services/logs")
    suspend fun getLog() : Response<WrappedListResponse<LogDto>>
}