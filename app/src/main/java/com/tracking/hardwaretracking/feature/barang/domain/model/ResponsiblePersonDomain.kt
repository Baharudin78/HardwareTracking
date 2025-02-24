package com.tracking.hardwaretracking.feature.barang.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ResponsiblePersonDomain(
    val name : String,
    val username : String,
    val status : String
) : Parcelable
