package com.tracking.hardwaretracking.feature.barang.domain.request


import com.google.gson.annotations.SerializedName

data class UpdateBarangRequest(
    @SerializedName("responsible_person")
    val responsiblePerson: String?,
    @SerializedName("current_location")
    val currentLocation : String?,
    @SerializedName("desc_location")
    val descLocation : String?,
    @SerializedName("qrcode")
    val qrcode : String?,
)