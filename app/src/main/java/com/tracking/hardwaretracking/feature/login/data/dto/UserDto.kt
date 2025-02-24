package com.tracking.hardwaretracking.feature.login.data.dto


import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("custom_id")
    val customId: Any?,
    @SerializedName("deletedAt")
    val deletedAt: Any?,
    @SerializedName("EH")
    val eH: String?,
    @SerializedName("flag")
    val flag: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("username")
    val username: String?
)