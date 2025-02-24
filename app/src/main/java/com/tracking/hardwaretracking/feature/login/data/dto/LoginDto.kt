package com.tracking.hardwaretracking.feature.login.data.dto


import com.google.gson.annotations.SerializedName
import com.tracking.hardwaretracking.feature.login.domain.model.LoginDomain

data class LoginDto(
    @SerializedName("token")
    val token: String?,
    @SerializedName("role")
    val role : String?,
    @SerializedName("name")
    val name : String?,
    @SerializedName("email")
    val email : String?,
    @SerializedName("id")
    val id : Int?,
)