package com.tracking.hardwaretracking.feature.barang.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BarangDomain(
    val categoryId: Int?,
    val createdAt: String?,
    val currentLocation: String?,
    val descLocation: String?,
    val encryptQrcode: String?,
    val hakMilik: String?,
    val id: Int?,
    val name: String?,
    val qrcode: String?,
    val responsiblePersonId: Int?,
    val role: String?,
    val status: String?,
    val updatedAt: String?,
    val responsiblePerson : ResponsiblePersonDomain?
) :Parcelable
