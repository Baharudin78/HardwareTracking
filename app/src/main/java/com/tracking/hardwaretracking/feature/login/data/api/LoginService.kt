package com.tracking.hardwaretracking.feature.login.data.api

import com.tracking.hardwaretracking.core.WrappedResponse
import com.tracking.hardwaretracking.feature.login.data.dto.LoginDto
import com.tracking.hardwaretracking.feature.login.domain.request.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("auth/login")
    suspend fun postLogin(@Body request: LoginRequest) : Response<WrappedResponse<LoginDto>>
}