package com.tracking.hardwaretracking.feature.barang.data.dto


import com.google.gson.annotations.SerializedName

data class BarangDto(
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("qrcode")
    val qrcode: String?,
    @SerializedName("responsible_person")
    val responsiblePerson: String?
)