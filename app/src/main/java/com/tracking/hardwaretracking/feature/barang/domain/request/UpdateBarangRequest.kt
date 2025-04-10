package com.tracking.hardwaretracking.feature.barang.domain.request


import com.google.gson.annotations.SerializedName

data class UpdateBarangRequest(
    @SerializedName("category_id")
    val categoryId: Int?,
    @SerializedName("current_location")
    val currentLocation: String?,
    @SerializedName("desc_location")
    val descLocation: String?,
    @SerializedName("hak_milik")
    val hakMilik: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("note")
    val note: String?,
    @SerializedName("qrcode")
    val qrcode: String?,
    @SerializedName("responsible_person_id")
    val responsiblePersonId: Int?
)