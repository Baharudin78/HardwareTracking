package com.tracking.hardwaretracking.feature.login.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracking.hardwaretracking.core.BaseResult
import com.tracking.hardwaretracking.core.WrappedListResponse
import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.barang.data.dto.BarangDto
import com.tracking.hardwaretracking.feature.barang.data.mapper.toDomain
import com.tracking.hardwaretracking.feature.barang.domain.model.BarangDomain
import com.tracking.hardwaretracking.feature.login.data.api.LoginService
import com.tracking.hardwaretracking.feature.login.data.dto.LoginDto
import com.tracking.hardwaretracking.feature.login.data.dto.UserDto
import com.tracking.hardwaretracking.feature.login.data.mapper.toDomain
import com.tracking.hardwaretracking.feature.login.data.mapper.toLoginEntity
import com.tracking.hardwaretracking.feature.login.domain.model.LoginDomain
import com.tracking.hardwaretracking.feature.login.domain.model.UserDomain
import com.tracking.hardwaretracking.feature.login.domain.repository.LoginRepository
import com.tracking.hardwaretracking.feature.login.domain.request.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService
)  : LoginRepository {
    override suspend fun postLogin(loginRequest: LoginRequest): Flow<BaseResult<LoginDomain, WrappedResponse<LoginDto>>> {
        return flow {
            try {
                val response = loginService.postLogin(loginRequest)
                if (response.isSuccessful) {
                    val body = response.body()!!
                    val login = body.data.let { it!!.toLoginEntity() }
                    emit(BaseResult.Success(login))
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

    override suspend fun getListUser(): Flow<BaseResult<List<UserDomain>, WrappedListResponse<UserDto>>> {
        return flow {
            val response = loginService.getListUser()
            if (response.isSuccessful){
                val body = response.body()!!
                val users = mutableListOf<UserDomain>()
                body.data?.forEach { menuResponse ->
                    users.add(
                        menuResponse.toDomain()
                    )
                }
                emit(BaseResult.Success(users))
            }else{
                val type = object : TypeToken<WrappedListResponse<UserDto>>(){}.type
                val err = Gson().fromJson<WrappedListResponse<UserDto>>(response.errorBody()!!.charStream(), type)!!
                emit(BaseResult.Error(err))
            }
        }
    }

    private fun parseErrorResponse(response: Response<*>): WrappedResponse<LoginDto> {
        val type = object : TypeToken<WrappedResponse<LoginDto>>() {}.type
        val errorBody =
            response.errorBody() ?: return WrappedResponse(message = "Unknown error occurred", null)
        return Gson().fromJson(errorBody.charStream(), type)!!
    }
}