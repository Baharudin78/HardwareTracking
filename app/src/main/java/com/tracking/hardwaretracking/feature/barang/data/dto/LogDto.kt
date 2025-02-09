package com.tracking.hardwaretracking.feature.barang.data.dto

import com.google.gson.annotations.SerializedName
import com.tracking.hardwaretracking.feature.login.data.dto.UserDto

data class LogDto(
    @SerializedName("barang")
    val barang: BarangDto?,
    @SerializedName("barang_id")
    val barangId: Int?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("location_from")
    val locationFrom: String?,
    @SerializedName("location_to")
    val locationTo: String?,
    @SerializedName("note")
    val note: String?,
    @SerializedName("responsible_from")
    val responsibleFrom: String?,
    @SerializedName("responsible_from_id")
    val responsibleFromId: String?,
    @SerializedName("responsible_to")
    val responsibleTo: String?,
    @SerializedName("responsible_to_id")
    val responsibleToId: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("user")
    val user: UserDto?,
    @SerializedName("user_id")
    val userId: Int?
)