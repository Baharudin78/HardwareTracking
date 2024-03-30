package com.tracking.hardwaretracking.feature.barang.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BarangDomain(
    val id: String,
    val name: String,
    val qrcode: String,
    val responsiblePerson: String,
    val currentLocation: String,
    val descLocation: String,
    val encryptQrcode: String,
) :Parcelable
