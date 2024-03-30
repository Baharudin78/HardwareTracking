package com.tracking.hardwaretracking.feature.barang.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.barang.data.api.BarangService
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.data.mapper.toDomain
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.barang.domain.repository.BarangRepository
import com.tracking.hardwaretracking.feature.barang.domain.request.UpdateBarangRequest
import com.tracking.hardwaretracking.feature.login.data.dto.LoginDto
import com.tracking.hardwaretracking.feature.login.data.mapper.toLoginEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class BarangRepositoryImpl @Inject constructor(
    private val barangService: BarangService
) : BarangRepository{
    override suspend fun getListBarang(): Flow<BaseResult<List<BarangDomain>, WrappedListResponse<BarangDto>>> {
        return flow {
            val response = barangService.getListBarang()
            if (response.isSuccessful){
                val body = response.body()!!
                val barang = mutableListOf<BarangDomain>()
                body.data?.forEach { menuResponse ->
                    barang.add(
                        menuResponse.toDomain()
                    )
                }
                emit(BaseResult.Success(barang))
            }else{
                val type = object : TypeToken<WrappedListResponse<BarangDto>>(){}.type
                val err = Gson().fromJson<WrappedListResponse<BarangDto>>(response.errorBody()!!.charStream(), type)!!
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun updateBarang(
        id: String,
        request: UpdateBarangRequest
    ): Flow<BaseResult<BarangDomain, WrappedResponse<BarangDto>>> {
        return flow {
            try {
                val response = barangService.updateBarang(id, request)
                if (response.isSuccessful) {
                    val body = response.body()!!
                    val update = body.data.let { it!!.toDomain() }
                    emit(BaseResult.Success(update))
                } else {
                    val errorResponse = parseErrorResponse(response)
                    emit(BaseResult.Error(errorResponse))
                }
            }catch (error : Exception) {
                emit(
                    BaseResult.Error(
                        WrappedResponse(
                            message = error.message ?: "Error Occured",
                            null
                        )
                    )
                )
            }
        }
    }

    private fun parseErrorResponse(response: Response<*>): WrappedResponse<BarangDto> {
        val type = object : TypeToken<WrappedResponse<BarangDto>>() {}.type
        val errorBody =
            response.errorBody() ?: return WrappedResponse(message = "Unknown error occurred", null)
        return Gson().fromJson(errorBody.charStream(), type)!!
    }
}