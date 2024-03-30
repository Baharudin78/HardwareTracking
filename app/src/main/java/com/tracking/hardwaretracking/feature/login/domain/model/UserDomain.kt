package com.tracking.hardwaretracking.feature.login.domain.model

import com.google.gson.annotations.SerializedName

data class UserDomain(
    @SerializedName("email")
    val email: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)
