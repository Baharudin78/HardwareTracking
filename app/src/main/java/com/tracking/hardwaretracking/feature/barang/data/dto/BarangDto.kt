package com.tracking.hardwaretracking.feature.barang.data.dto


import com.google.gson.annotations.SerializedName

data class BarangDto(
    @SerializedName("category_id")
    val categoryId: Int?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("current_location")
    val currentLocation: String?,
    @SerializedName("deletedAt")
    val deletedAt: Any?,
    @SerializedName("desc_location")
    val descLocation: String?,
    @SerializedName("encrypt_qrcode")
    val encryptQrcode: String?,
    @SerializedName("hak_milik")
    val hakMilik: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("qrcode")
    val qrcode: String?,
    @SerializedName("responsible_person_id")
    val responsiblePersonId: Int?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("responsible_person")
    val responsiblePerson : ResponsiblePersonDto?
)